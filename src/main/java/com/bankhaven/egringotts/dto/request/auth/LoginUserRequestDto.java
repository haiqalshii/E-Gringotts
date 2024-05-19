package com.bankhaven.egringotts.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserRequestDto {

    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Email field cannot be empty.")
    private String email;

    @NotBlank(message = "Password field cannot be empty.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;
}
