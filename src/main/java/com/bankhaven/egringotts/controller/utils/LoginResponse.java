package com.bankhaven.egringotts.controller.utils;

import com.bankhaven.egringotts.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn;
    private UserRole role;
}
