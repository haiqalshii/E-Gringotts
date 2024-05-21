package com.bankhaven.egringotts.controller;

import com.bankhaven.egringotts.dto.model.AccountDto;
import com.bankhaven.egringotts.dto.model.TransactionDto;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.account.AccountDeleteRequestDto;
import com.bankhaven.egringotts.dto.request.account.UpdateAccountRequestDto;
import com.bankhaven.egringotts.dto.request.auth.NewAccountRequestDto;
import com.bankhaven.egringotts.dto.request.currencyconversion.AddConversionRateRequestDto;
import com.bankhaven.egringotts.dto.request.pensievepast.AdminTransactionHistoryRequestDto;
import com.bankhaven.egringotts.dto.request.pensievepast.TransactionHistoryRequestDto;
import com.bankhaven.egringotts.dto.request.user.UpdateUserRequestDto;
import com.bankhaven.egringotts.dto.request.user.UserDeleteRequestDto;
import com.bankhaven.egringotts.service.AccountService;
import com.bankhaven.egringotts.service.CurrencyConversionService;
import com.bankhaven.egringotts.service.TransactionService;
import com.bankhaven.egringotts.service.UserService;
import com.bankhaven.egringotts.dto.request.currencyconversion.AddCurrencyRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminOperationsController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final CurrencyConversionService currencyConversionService;

    public AdminOperationsController(AccountService accountService, TransactionService transactionService, UserService userService, CurrencyConversionService currencyConversionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.currencyConversionService = currencyConversionService;
    }


//    @PostMapping("/create")
//    public ResponseEntity<Void> createAccountForUser(@RequestBody @Valid NewAccountRequestDto newAccountRequest,
//                                                     UriComponentsBuilder ucb) {
//        AccountDto accountDto = accountService.createNewAccountForUser(newAccountRequest);
//
//        URI locationOfNewAccount = ucb.path("/accounts/{id}").buildAndExpand(accountDto.getId()).toUri();
//        return ResponseEntity.created(locationOfNewAccount).build();
//    }

    @PostMapping("/create")
    public ResponseEntity<String> createAccountForUser(@RequestBody @Valid NewAccountRequestDto newAccountRequest) {
        String message = accountService.createNewAccountForUser(newAccountRequest);
        return ResponseEntity.ok(message);
    }

    // Allow only ADMIN to delete account
    @DeleteMapping("/accounts/delete")
    public ResponseEntity<String> deleteAccount(@RequestBody @Valid AccountDeleteRequestDto accountDeleteRequestDto) {
        String message = accountService.deleteAccountById(accountDeleteRequestDto.getAccountId());
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(@RequestBody @Valid UserDeleteRequestDto userDeleteRequestDto) {
        String message = userService.deleteUserById(userDeleteRequestDto.getUserId());
        return ResponseEntity.ok(message);
    }

    // Allow admin to show ALL accounts of all users
    @GetMapping("/accounts/display-all")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        // Method accessible only to users with the role ROLE_ADMIN
        List<AccountDto> accounts = accountService.getAllAccountsAdmin();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/display-transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsAdmin(
            @RequestBody AdminTransactionHistoryRequestDto adminTransactionHistoryRequestDto) {
        // Assuming the userId is not needed for the admin as they can view any account
        String adminUserId = ""; // Or obtain it from the security context if needed
        List<TransactionDto> transactions = transactionService.getAllTransactionsAdmin(adminUserId, adminTransactionHistoryRequestDto);
        return ResponseEntity.ok(transactions);
    }

    // Allow admin to show ALL users
    @GetMapping("/user/display-all")
    public ResponseEntity<List<UserDto>> getAllUsersAdmin() {
        List<UserDto> users = userService.getAllUsersAdmin();
        return ResponseEntity.ok(users);
    }

    // Allow admin to update accounts of user
    @PutMapping("/accounts/update")
    public ResponseEntity<String> updateAccount(@RequestBody UpdateAccountRequestDto updateAccountRequest) {
        // Method accessible only to users with the role ROLE_ADMIN
        String message = accountService.updateAccountDetails(updateAccountRequest);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/user/update")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequestDto updateUserRequest) {
        String message = userService.updateUserDetails(updateUserRequest);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/currency-conversion/add-conversion-rate")
    public ResponseEntity<Void> addConversionRate(@RequestBody AddConversionRateRequestDto requestDto) {
        currencyConversionService.addConversionRate(requestDto.getFromCurrency(), requestDto.getToCurrency(), requestDto.getConversionRate(), requestDto.getFee());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/currency-conversion/add-currency")
    public ResponseEntity<Void> addCurrency(@RequestBody AddCurrencyRequestDto requestDto) {
        currencyConversionService.addCurrency(requestDto.getCurrencyCode());
        return ResponseEntity.ok().build();
    }

}
