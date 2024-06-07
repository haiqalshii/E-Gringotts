package com.bankhaven.egringotts.controller;

import com.bankhaven.egringotts.controller.utils.LoginResponse;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.auth.LoginUserRequestDto;
import com.bankhaven.egringotts.dto.request.auth.RegisterUserRequestDto;
import com.bankhaven.egringotts.dto.request.transaction.NewDepositMoneyRequestDto;
import com.bankhaven.egringotts.model.User;
import com.bankhaven.egringotts.model.enums.UserRole;
import com.bankhaven.egringotts.service.AuthenticationService;
import com.bankhaven.egringotts.service.JwtUtils;
import com.bankhaven.egringotts.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtils jwtUtils;
    private final AuthenticationService authenticationService;
    private final TransactionService transactionService;


    public AuthenticationController(JwtUtils jwtUtils, AuthenticationService authenticationService, TransactionService transactionService) {
        this.jwtUtils = jwtUtils;
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
    }

    //register a new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterUserRequestDto registerUserDto) {
        String message = authenticationService.register(registerUserDto);
        return ResponseEntity.ok(message);
    }

    //login user
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserRequestDto loginUserDto) {
        User authenticatedUser = authenticationService.login(loginUserDto);

        // Obtain the user's role from the authenticatedUser object
        UserRole userRole = authenticatedUser.getRole(); // Adjust this according to your User class structure

        // Generate the JWT token with the user's role
        String jwtToken = jwtUtils.generateToken(authenticatedUser, userRole);

        // Create the login response
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtUtils.getExpirationTime(), userRole);

        // Return the response with the JWT token
        return ResponseEntity.ok(loginResponse);
    }
//
//    @PostMapping("/accounts/deposit-money")
//    public ResponseEntity<String> depositMoney(@RequestBody @Valid NewDepositMoneyRequestDto depositMoneyRequestDto) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String receipt = transactionService.depositMoney(depositMoneyRequestDto, user.getId());
////
////        System.out.println("Deposit Money Request DTO: " + depositMoneyRequestDto);
////        System.out.println("User: " + user);
////        System.out.println("Receipt: " + receipt);
//        return ResponseEntity.ok(receipt);
//    }
}
