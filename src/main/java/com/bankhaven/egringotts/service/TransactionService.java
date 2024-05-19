package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.TransactionDto;
import com.bankhaven.egringotts.dto.model.TransactionUserDto;
import com.bankhaven.egringotts.dto.request.pensievepast.AdminTransactionHistoryRequestDto;
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

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, AccountService accountService, EmailService emailService, ModelMapper modelMapper) {
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

        // Create a new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionType(TransactionType.DEPOSIT);
        newTransaction.setDescription(depositMoneyRequestDto.getDescription());
        newTransaction.setReceiverAccount(currentAccount); // Set the receiver account
        newTransaction.setAmount(depositMoneyRequestDto.getAmount());

        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(newTransaction);

        // Generate receipt for the deposit transaction
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

        // Deduct the withdrawn amount from the current account balance
        currentAccount.setBalance(currentAccount.getBalance().subtract(withdrawMoneyRequestDto.getAmount()));
        accountRepository.save(currentAccount);

        // Create a new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionType(TransactionType.WITHDRAW);
        newTransaction.setDescription(withdrawMoneyRequestDto.getDescription());
        newTransaction.setSenderAccount(currentAccount); // Set the sender account
        newTransaction.setAmount(withdrawMoneyRequestDto.getAmount());

        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(newTransaction);

        // Generate receipt for the withdrawal transaction
        String receipt = emailService.generateReceiptForWithdraw(savedTransaction);
        emailService.sendReceiptEmail(currentAccount.getUser().getEmail(), "E-Gringotts Withdrawal Receipt", receipt);

        return "Withdrawal successful. A receipt has been sent to your email.";
    }

    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }



    @NonNull
    private List<TransactionDto> getTransactionDtos(List<Transaction> transactions) {
        return transactions.stream().map(transaction -> {
            // Null checks for sender and receiver accounts
            User receiverUser = (transaction.getReceiverAccount() != null) ? transaction.getReceiverAccount().getUser() : null;
            User senderUser = (transaction.getSenderAccount() != null) ? transaction.getSenderAccount().getUser() : null;

            // Map the transaction to TransactionDto
            TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);

            // Map sender user if not null, otherwise set a default or empty DTO
            if (senderUser != null) {
                transactionDto.setSenderUser(modelMapper.map(senderUser, TransactionUserDto.class));
            } else {
                transactionDto.setSenderUser(null); // No sender for deposits
            }

            // Map receiver user if not null, otherwise set a default or empty DTO
            if (receiverUser != null) {
                transactionDto.setReceiverUser(modelMapper.map(receiverUser, TransactionUserDto.class));
            } else {
                transactionDto.setReceiverUser(null); // No receiver for withdrawals
            }

            return transactionDto;
        }).toList();
    }

    public List<TransactionDto> getTransactionHistory(String userId, TransactionHistoryRequestDto transactionHistoryRequestDto) {

        String accountId = accountRepository.findIdByAccountNumber(transactionHistoryRequestDto.getAccountNumber());
        // Ensure the account exists and is valid for the given user
        accountService.findAccountById(accountId, userId);

        // Fetch all transactions related to the account, including those with null sender or receiver accounts
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountId(accountId);

        // Apply filtering by category if provided
        if (transactionHistoryRequestDto.getFilterByCategory() != null && !transactionHistoryRequestDto.getFilterByCategory().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> transactionHistoryRequestDto.getFilterByCategory().equalsIgnoreCase(String.valueOf(transaction.getTransactionCategory())))
                    .collect(Collectors.toList());
        }

        // Apply filtering by transaction type if provided
        if (transactionHistoryRequestDto.getFilterByType() != null && !transactionHistoryRequestDto.getFilterByType().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> transactionHistoryRequestDto.getFilterByType().equalsIgnoreCase(String.valueOf(transaction.getTransactionType())))
                    .collect(Collectors.toList());
        }

        // Apply sorting based on the sortBy parameter
        if (transactionHistoryRequestDto.getSortBy() != null && !transactionHistoryRequestDto.getSortBy().isEmpty()) {
            switch (transactionHistoryRequestDto.getSortBy().toLowerCase()) {
                case "amount":
                    transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
                    break;
                case "date":
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
                default:
                    // Default sorting by date
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
            }
        } else {
            // Default sorting by date
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        }

        // Map the transactions to TransactionDto objects
        return getTransactionDtos(transactions);
    }

    public List<TransactionDto> getAllTransactionsAdmin(String userId, AdminTransactionHistoryRequestDto adminTransactionHistoryRequestDto) {
        List<Transaction> transactions;

        if (adminTransactionHistoryRequestDto.getAccountId() == null || adminTransactionHistoryRequestDto.getAccountId().isEmpty()) {
            // Fetch all transactions if accountId is null or empty
            transactions = transactionRepository.findAll();
        } else {
            // Fetch transactions for the specific account
            transactions = transactionRepository.findAllTransactionsByAccountId(adminTransactionHistoryRequestDto.getAccountId());
        }

        // Apply filtering by category if provided
        if (adminTransactionHistoryRequestDto.getFilterByCategory() != null && !adminTransactionHistoryRequestDto.getFilterByCategory().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> adminTransactionHistoryRequestDto.getFilterByCategory().equalsIgnoreCase(String.valueOf(transaction.getTransactionCategory())))
                    .collect(Collectors.toList());
        }

        // Apply filtering by transaction type if provided
        if (adminTransactionHistoryRequestDto.getFilterByType() != null && !adminTransactionHistoryRequestDto.getFilterByType().isEmpty()) {
            transactions = transactions.stream()
                    .filter(transaction -> adminTransactionHistoryRequestDto.getFilterByType().equalsIgnoreCase(String.valueOf(transaction.getTransactionType())))
                    .collect(Collectors.toList());
        }

        // Apply sorting based on the sortBy parameter
        if (adminTransactionHistoryRequestDto.getSortBy() != null && !adminTransactionHistoryRequestDto.getSortBy().isEmpty()) {
            switch (adminTransactionHistoryRequestDto.getSortBy().toLowerCase()) {
                case "amount":
                    transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
                    break;
                case "date":
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
                default:
                    // Default sorting by date
                    transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                    break;
            }
        } else {
            // Default sorting by date
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        }

        // Map the transactions to TransactionDto objects
        return getTransactionDtos(transactions);
    }


    public List<TransactionDto> sortByAmountHighToLow(List<TransactionDto> transactions) {
        transactions.sort(Comparator.comparing(TransactionDto::getAmount).reversed());
        return transactions;
    }

//        // Print the transaction details for debugging
//        printTransactions(transactions);

//        // Ensure the account exists and is valid for the given user
//        accountService.findAccountById(accountId, userId);
//
//        // Fetch the transactions related to the account
//        Page<Transaction> transactionPage = transactionRepository.findBySenderAccountIdOrReceiverAccountId(
//                accountId,
//                accountId,
//                PageRequest.of(
//                        pageable.getPageNumber(),
//                        pageable.getPageSize(),
//                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "date"))
//                )
//        );
//
//        // Extract the content of the page as a list of transactions
//        List<Transaction> transactions = transactionPage.getContent();

//    public List<TransactionDto> getTransactionHistory(String accountId, String userId, Pageable pageable) {
//
//        accountService.findAccountById(accountId, userId);
//
//        Page<Transaction> transactionPage = transactionRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId,
//                PageRequest.of(pageable.getPageNumber(),
//                        pageable.getPageSize(),
//                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "date"))
//                ));
//
//        List<Transaction> transactions = transactionPage.getContent();
//
//
//        return transactions.stream().map(transaction -> {
//
//            User receiverUser = transaction.getReceiverAccount().getUser();
//            User senderUser = transaction.getSenderAccount().getUser();
//            TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);
//            transactionDto.setSenderUser(modelMapper.map(senderUser, TransactionUserDto.class));
//            transactionDto.setReceiverUser(modelMapper.map(receiverUser, TransactionUserDto.class));
//            return transactionDto;
//
//        }).toList();
//
//    }
}
