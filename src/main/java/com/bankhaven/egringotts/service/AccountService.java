package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.AccountDto;
import com.bankhaven.egringotts.dto.model.AddressDto;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.account.UpdateAccountRequestDto;
import com.bankhaven.egringotts.dto.request.auth.NewAccountRequestDto;
import com.bankhaven.egringotts.dto.request.user.ViewAccountDetailsRequestDto;
import com.bankhaven.egringotts.exception.AccountNotFoundException;
import com.bankhaven.egringotts.exception.AccountTierNotExistException;
import com.bankhaven.egringotts.exception.InsufficientInitialFundException;
import com.bankhaven.egringotts.exception.UserNotFoundException;
import com.bankhaven.egringotts.model.Account;
import com.bankhaven.egringotts.model.Address;
import com.bankhaven.egringotts.model.Transaction;
import com.bankhaven.egringotts.model.User;
import com.bankhaven.egringotts.model.enums.AccountTier;
import com.bankhaven.egringotts.model.enums.TransactionType;
import com.bankhaven.egringotts.repository.AccountRepository;
import com.bankhaven.egringotts.repository.AddressRepository;
import com.bankhaven.egringotts.repository.TransactionRepository;
import com.bankhaven.egringotts.repository.UserRepository;
import com.bankhaven.egringotts.service.interfaces.AccountServiceInterface;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService implements AccountServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EmailService emailService;
    private final TransactionRepository transactionRepository;

    public AccountService(ModelMapper modelMapper, AccountRepository accountRepository, UserRepository userRepository, AddressRepository addressRepository, EmailService emailService, TransactionRepository transactionRepository) {
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.emailService = emailService;
        this.transactionRepository = transactionRepository;
    }


    @Override
    public String createNewAccountForUser(NewAccountRequestDto newAccountRequest) {
        validateNewAccountRequest(newAccountRequest);
        User user = userRepository.findById(newAccountRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + newAccountRequest.getUserId()));

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setBalance(newAccountRequest.getInitialBalance());
        newAccount.setAccountNumber(generateAccountNumber());
        newAccount.setAccountTier(newAccountRequest.getAccountTier());

        Account savedAccount = accountRepository.save(newAccount);

        if (newAccountRequest.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {
            Transaction transaction = new Transaction();
            transaction.setSenderAccount(null);
            transaction.setReceiverAccount(savedAccount);
            transaction.setAmount(newAccountRequest.getInitialBalance());
            transaction.setDescription("Initial transaction.");
            transaction.setTransactionType(TransactionType.INITIAL);
            transactionRepository.save(transaction);
        }

        AccountDto accountDto = modelMapper.map(savedAccount, AccountDto.class);

        // Prepare account details for the email
        String accountDetails = String.format("Account Number: %s\nBalance: %s\nAccount Tier: %s",
                savedAccount.getAccountNumber(), savedAccount.getBalance(), savedAccount.getAccountTier());

        emailService.sendAccountCreationMessage(user.getEmail(), accountDetails);

        return "Account created successfully. Check your email.";
    }

    protected String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    public List<AccountDto> getAllAccountsGoblin() {
        List<Account> accounts = accountRepository.findAllAccounts();

        return accounts.stream().map(account -> {
            // Get the user associated with the account
            User accountUser = account.getUser();

            // Fetch the address associated with the user
            Address address = addressRepository.findByUserId(accountUser.getId());

            // Map the address to an AddressDto
            AddressDto addressDto = modelMapper.map(address, AddressDto.class);

            // Map the user to a UserDto including the address
            UserDto userDto = modelMapper.map(accountUser, UserDto.class);
            userDto.setAddress(addressDto);

            // Map the account to an AccountDto including the user
            AccountDto accountDto = modelMapper.map(account, AccountDto.class);
            accountDto.setUserDto(userDto);

            return accountDto;
        }).collect(Collectors.toList());
    }

    public AccountDto getAccountById(ViewAccountDetailsRequestDto requestDto, String userId) {
        String accountId = requestDto.getAccountId();
        return accountRepository.findById(accountId)
                .filter(account -> account.getUser().getId().equals(userId))
                .map(account -> modelMapper.map(account, AccountDto.class))
                .orElseThrow(() -> new AccountNotFoundException("Account not found for id: " + accountId + " and user id: " + userId));
    }

    public Account findAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for the given ID."));
    }


    protected Account findAccountById(String accountId, String userId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("Account not found for id : " + accountId);
        }
        Account account = accountOptional.get();

        if (!account.getUser().getId().equals(userId)) {
            throw new AccountNotFoundException("Account not found for id : " + accountId + " for user id : " + userId);
        }
        return account;
    }

    protected Account findAccountByAccountNumber(String accountNumber, String userId) {
        Optional<Account> accountOptional = accountRepository.findAccountByAccountNumber(accountNumber);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("Account not found for account number: " + accountNumber);
        }
        Account account = accountOptional.get();

        if (!account.getUser().getId().equals(userId)) {
            throw new AccountNotFoundException("Account not found for account number: " + accountNumber + " for user id: " + userId);
        }
        return account;
    }

    public List<AccountDto> getAllAccounts(User user) {
        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        return accounts.stream().map(account -> {
            // Get the user associated with the account
            User accountUser = account.getUser();

            // Fetch the address associated with the user
            Address address = addressRepository.findByUserId(accountUser.getId());

            // Map the address to an AddressDto
            AddressDto addressDto = modelMapper.map(address, AddressDto.class);

            // Map the user to a UserDto including the address
            UserDto userDto = modelMapper.map(accountUser, UserDto.class);
            userDto.setAddress(addressDto);

            // Map the account to an AccountDto including the user
            AccountDto accountDto = modelMapper.map(account, AccountDto.class);
            accountDto.setUserDto(userDto);

            return accountDto;
        }).collect(Collectors.toList());
    }

    public String deleteAccountById(String id) {
        Account account = findAccountById(id);

        // Delete all transactions associated with the account
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountId(id);
        transactionRepository.deleteAll(transactions);

        // Now delete the account
        accountRepository.delete(account);

        return "Account deleted successfully";
    }

    public String updateAccountDetails(UpdateAccountRequestDto updateAccountRequest) {
        Account account = accountRepository.findById(updateAccountRequest.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setAccountTier(updateAccountRequest.getAccountTier());
        account.setBalance(updateAccountRequest.getBalance());

        accountRepository.save(account);
        return "Account updated successfully";
    }

    private void validateNewAccountRequest(NewAccountRequestDto newAccountRequest) {
        AccountTier accountTier = newAccountRequest.getAccountTier();
        BigDecimal initialBalance = newAccountRequest.getInitialBalance();

        // Validate initial balance
        BigDecimal minimumInitialFund = getMinimumInitialBalance(accountTier);
        if (initialBalance.compareTo(minimumInitialFund) < 0) {
            throw new InsufficientInitialFundException("Initial balance for " + accountTier + " account must be at least " + minimumInitialFund);
        }

        // Validate account tier
        if (accountTier != AccountTier.PLATINUM_PATRONUS && accountTier != AccountTier.GOLDEN_GALLEON && accountTier != AccountTier.SILVER_SNITCH) {
            throw new AccountTierNotExistException("Account tier does not exist: " + accountTier);
        }

    }

    private BigDecimal getMinimumInitialBalance(AccountTier accountTier) {
        switch (accountTier) {
            case PLATINUM_PATRONUS:
                return new BigDecimal("5000.00");
            case GOLDEN_GALLEON:
                return new BigDecimal("1000.00");
            case SILVER_SNITCH:
                return new BigDecimal("100.00");
            default:
                throw new IllegalArgumentException("Invalid account tier: " + accountTier);
        }
    }


    public List<String> getAccountNumbersByUserInfo(String userInfo) {
        List<User> users = userRepository.findByEmailOrPhoneNumberOrName(userInfo, userInfo, userInfo);

        List<String> accountNumbers = new ArrayList<>();
        for (User user : users) {
            List<Account> accounts = accountRepository.findAllByUserId(user.getId());
            for (Account account : accounts) {
                accountNumbers.add(account.getAccountNumber());
            }
        }
        return accountNumbers;
    }
}
