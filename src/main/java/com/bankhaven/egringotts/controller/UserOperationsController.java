package com.bankhaven.egringotts.controller;

import com.bankhaven.egringotts.dto.model.AccountDto;
import com.bankhaven.egringotts.dto.model.TransactionDto;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.account.SearchRequestDto;
import com.bankhaven.egringotts.dto.request.currencyconversion.NewCurrencyConversionRequestDto;
import com.bankhaven.egringotts.dto.request.pensievepast.TransactionHistoryRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewDepositMoneyRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewMoneyTransferRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewWithdrawMoneyRequestDto;
import com.bankhaven.egringotts.dto.request.user.ViewAccountDetailsRequestDto;
import com.bankhaven.egringotts.model.User;
import com.bankhaven.egringotts.service.AccountService;
import com.bankhaven.egringotts.service.CurrencyConversionService;
import com.bankhaven.egringotts.service.TransactionService;
import com.bankhaven.egringotts.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserOperationsController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final CurrencyConversionService currencyConversionService;


    public UserOperationsController(AccountService accountService, TransactionService transactionService, UserService userService, CurrencyConversionService currencyConversionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.currencyConversionService = currencyConversionService;
    }

    // Allow user to view his account detail
    @PostMapping("/accounts/account-details")
    public ResponseEntity<AccountDto> getAccount(@RequestBody @Valid ViewAccountDetailsRequestDto requestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AccountDto accountDto = accountService.getAccountById(requestDto, user.getId());
        return ResponseEntity.ok(accountDto);
    }

    // Allow user to view his accounts
    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountDto>> getUserAccounts() {
        // Method accessible to all authenticated users
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(accountService.getAllAccounts(user));
    }

    @PostMapping("/currency-conversion/convert")
    public ResponseEntity<String> convertCurrencyAndDeductBalance(@RequestBody NewCurrencyConversionRequestDto conversionRequestDto) {
        // Extract the user ID from the security context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Call the conversion service
        String response = currencyConversionService.convertCurrency(conversionRequestDto, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/transaction-history")
    public ResponseEntity<List<TransactionDto>> getTransactionHistory(@Valid @RequestBody TransactionHistoryRequestDto requestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TransactionDto> transactions = transactionService.getTransactionHistory(user.getId(), requestDto);

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/accounts/transfer-money")
    public ResponseEntity<String> transferMoney(@RequestBody @Valid NewMoneyTransferRequestDto transferRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String receipt = transactionService.transferMoney(transferRequestDto, user.getId());
        return ResponseEntity.ok(receipt);
    }


    @PostMapping("/accounts/deposit-money")
    public ResponseEntity<String> depositMoney(@RequestBody @Valid NewDepositMoneyRequestDto depositMoneyRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String receipt = transactionService.depositMoney(depositMoneyRequestDto, user.getId());
        return ResponseEntity.ok(receipt);
    }

    @PostMapping("/accounts/withdraw-money")
    public ResponseEntity<String> withdrawMoney(@RequestBody @Valid NewWithdrawMoneyRequestDto withdrawMoneyRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String receipt = transactionService.withdrawMoney(withdrawMoneyRequestDto, user.getId());
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getCurrentUser(currentUser));
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchAccounts(@RequestBody SearchRequestDto searchRequestDto) {
        List<String> accountNumbers = accountService.getAccountNumbersByUserInfo(searchRequestDto.getUserInfo());
        return ResponseEntity.ok(accountNumbers);
    }
}
