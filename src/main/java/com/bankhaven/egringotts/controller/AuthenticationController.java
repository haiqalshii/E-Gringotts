package com.bankhaven.egringotts.controller;

import com.bankhaven.egringotts.controller.utils.LoginResponse;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.auth.LoginUserRequestDto;
import com.bankhaven.egringotts.dto.request.auth.RegisterUserRequestDto;
import com.bankhaven.egringotts.model.User;
import com.bankhaven.egringotts.service.AuthenticationService;
import com.bankhaven.egringotts.service.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtils jwtUtils;
    private final AuthenticationService authenticationService;


    public AuthenticationController(JwtUtils jwtUtils, AuthenticationService authenticationService) {
        this.jwtUtils = jwtUtils;
        this.authenticationService = authenticationService;
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
        String jwtToken = jwtUtils.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtUtils.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
