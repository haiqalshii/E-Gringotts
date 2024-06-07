package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.TransactionDto;
import com.bankhaven.egringotts.dto.model.TransactionUserDto;
import com.bankhaven.egringotts.dto.request.pensievepast.GoblinTransactionHistoryRequestDto;
import com.bankhaven.egringotts.dto.request.pensievepast.TransactionHistoryRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewDepositMoneyRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewMoneyTransferRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewWithdrawMoneyRequestDto;
import com.bankhaven.egringotts.exception.AccountNotFoundException;
import com.bankhaven.egringotts.exception.InsufficientBalanceException;
import com.bankhaven.egringotts.model.Account;
import com.bankhaven.egringotts.model.Transaction;
import com.bankhaven.egringotts.model.User;
import com.bankhaven.egringotts.model.enums.TransactionType;
import com.bankhaven.egringotts.repository.AccountRepository;
import com.bankhaven.egringotts.repository.TransactionRepository;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                              AccountService accountService, EmailService emailService, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    public String transferMoney(NewMoneyTransferRequestDto transferRequestDto, String userId) {
        String senderAccountId = transferRequestDto.getSenderAccountNumber();

        // Validate sender account ID and retrieve the sender account
        Account senderAccount = accountService.findAccountByAccountNumber(senderAccountId, userId);
        if (senderAccount.getBalance().compareTo(transferRequestDto.getAmount()) < 0)
            throw new InsufficientBalanceException("Insufficient balance.");

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequestDto.getAmount()));
        accountRepository.save(senderAccount);

        Account receiverAccount = accountRepository.findAccountByAccountNumber(transferRequestDto.getReceiverAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found for the given account number."));

        receiverAccount.setBalance(receiverAccount.getBalance().add(transferRequestDto.getAmount()));
        accountRepository.save(receiverAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionType(TransactionType.TRANSFER);
        newTransaction.setTransactionCategory(transferRequestDto.getTransactionCategory());
        newTransaction.setDescription(transferRequestDto.getDescription());
        newTransaction.setReceiverAccount(receiverAccount);
        newTransaction.setSenderAccount(senderAccount);
        newTransaction.setAmount(transferRequestDto.getAmount());

        transactionRepository.save(newTransaction);

        String senderName = senderAccount.getUser().getFirstName() + " " + senderAccount.getUser().getLastName();
        String receiverName = receiverAccount.getUser().getFirstName() + " " + receiverAccount.getUser().getLastName();

        String receipt = emailService.generateReceiptForTransfer(newTransaction, senderName, receiverName);
        emailService.sendReceiptEmail(senderAccount.getUser().getEmail(), "E-Gringotts Transfer Receipt", receipt);

        return "Transfer successful. A receipt has been sent to your email.";
    }

    public String depositMoney(NewDepositMoneyRequestDto depositMoneyRequestDto, String userId) {
        String currentAccountId = depositMoneyRequestDto.getCurrentAccountNumber();

        // Validate receiver account ID and retrieve the receiver account
        Account currentAccount = accountService.findAccountByAccountNumber(currentAccountId, userId);

        // Add the deposited amount to the current account balance
        currentAccount.setBalance(currentAccount.getBalance().add(depositMoneyRequestDto.getAmount()));
        accountRepository.save(currentAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionType(TransactionType.DEPOSIT);
        newTransaction.setDescription(depositMoneyRequestDto.getDescription());
        newTransaction.setReceiverAccount(currentAccount); // Set the receiver account
        newTransaction.setAmount(depositMoneyRequestDto.getAmount());

        Transaction savedTransaction = transactionRepository.save(newTransaction);

        String receipt = emailService.generateReceiptForDeposit(savedTransaction);
        emailService.sendReceiptEmail(currentAccount.getUser().getEmail(), "E-Gringotts Deposit Receipt", receipt);

        return "Deposit successful. A receipt has been sent to your email.";
    }

    public String withdrawMoney(NewWithdrawMoneyRequestDto withdrawMoneyRequestDto, String userId) {
        String currentAccountId = withdrawMoneyRequestDto.getCurrentAccountNumber();

        // Validate sender account ID and retrieve the sender account
        Account currentAccount = accountService.findAccountByAccountNumber(currentAccountId, userId);
        if (currentAccount.getBalance().compareTo(withdrawMoneyRequestDto.getAmount()) < 0)
            throw new InsufficientBalanceException("Insufficient balance.");

        currentAccount.setBalance(currentAccount.getBalance().subtract(withdrawMoneyRequestDto.getAmount()));
        accountRepository.save(currentAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionType(TransactionType.WITHDRAW);
        newTransaction.setDescription(withdrawMoneyRequestDto.getDescription());
        newTransaction.setSenderAccount(currentAccount); // Set the sender account
        newTransaction.setAmount(withdrawMoneyRequestDto.getAmount());

        Transaction savedTransaction = transactionRepository.save(newTransaction);

        String receipt = emailService.generateReceiptForWithdraw(savedTransaction);
        emailService.sendReceiptEmail(currentAccount.getUser().getEmail(), "E-Gringotts Withdrawal Receipt", receipt);

        return "Withdrawal successful. A receipt has been sent to your email.";
    }


    @NonNull
    private List<TransactionDto> getTransactionDtos(List<Transaction> transactions) {
        return transactions.stream().map(transaction -> {
            User receiverUser = (transaction.getReceiverAccount() != null) ? transaction.getReceiverAccount().getUser() : null;
            User senderUser = (transaction.getSenderAccount() != null) ? transaction.getSenderAccount().getUser() : null;

            // Log transaction details for debugging
            System.out.println("Transaction ID: " + transaction.getId());
            System.out.println("Transaction Date: " + transaction.getDate());

            TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);

            if (transaction.getDate() == null) {
                // Log if the date is null for debugging purposes
                System.err.println("Transaction date is null for transaction ID: " + transaction.getId());
            } else {
                transactionDto.setDate(transaction.getDate().toLocalDate());
            }

            if (senderUser != null) {
                transactionDto.setSenderUser(modelMapper.map(senderUser, TransactionUserDto.class));
            } else {
                transactionDto.setSenderUser(null);
            }

            if (receiverUser != null) {
                transactionDto.setReceiverUser(modelMapper.map(receiverUser, TransactionUserDto.class));
            } else {
                transactionDto.setReceiverUser(null);
            }

            return transactionDto;
        }).toList();
    }

    public List<TransactionDto> getTransactionHistory(String userId, TransactionHistoryRequestDto transactionHistoryRequestDto) {
        String accountId = accountRepository.findIdByAccountNumber(transactionHistoryRequestDto.getAccountNumber());
        accountService.findAccountById(accountId, userId);

        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountId(accountId);

        if (transactionHistoryRequestDto.getFilterByCategory() != null && !transactionHistoryRequestDto.getFilterByCategory().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> transactionHistoryRequestDto.getFilterByCategory().equalsIgnoreCase(String.valueOf(transaction.getTransactionCategory())))
                    .collect(Collectors.toList());
        }

        if (transactionHistoryRequestDto.getFilterByType() != null && !transactionHistoryRequestDto.getFilterByType().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> transactionHistoryRequestDto.getFilterByType().equalsIgnoreCase(String.valueOf(transaction.getTransactionType())))
                    .collect(Collectors.toList());
        }

        if (transactionHistoryRequestDto.getSortBy() != null && !transactionHistoryRequestDto.getSortBy().isEmpty()) {
            switch (transactionHistoryRequestDto.getSortBy().toLowerCase()) {
                case "amount":
                    transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
                    break;
                case "date":
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
                default:
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
            }
        } else {
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        }

        return getTransactionDtos(transactions);
    }

    public List<TransactionDto> getAllTransactionsGoblin(String userId, GoblinTransactionHistoryRequestDto goblinTransactionHistoryRequestDto) {
        List<Transaction> transactions;

        if (goblinTransactionHistoryRequestDto.getAccountId() == null || goblinTransactionHistoryRequestDto.getAccountId().isEmpty()) {
            transactions = transactionRepository.findAll();
        } else {
            transactions = transactionRepository.findAllTransactionsByAccountId(goblinTransactionHistoryRequestDto.getAccountId());
        }

        if (goblinTransactionHistoryRequestDto.getFilterByCategory() != null && !goblinTransactionHistoryRequestDto.getFilterByCategory().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> goblinTransactionHistoryRequestDto.getFilterByCategory().equalsIgnoreCase(String.valueOf(transaction.getTransactionCategory())))
                    .collect(Collectors.toList());
        }

        if (goblinTransactionHistoryRequestDto.getFilterByType() != null && !goblinTransactionHistoryRequestDto.getFilterByType().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> goblinTransactionHistoryRequestDto.getFilterByType().equalsIgnoreCase(String.valueOf(transaction.getTransactionType())))
                    .collect(Collectors.toList());
        }

        if (goblinTransactionHistoryRequestDto.getSortBy() != null && !goblinTransactionHistoryRequestDto.getSortBy().isEmpty()) {
            switch (goblinTransactionHistoryRequestDto.getSortBy().toLowerCase()) {
                case "amount":
                    transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
                    break;
                case "date":
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
                default:
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
            }
        } else {
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        }

        return getTransactionDtos(transactions);
    }

}
