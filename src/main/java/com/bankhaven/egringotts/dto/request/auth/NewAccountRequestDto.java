package com.bankhaven.egringotts.dto.request.auth;

import com.bankhaven.egringotts.model.enums.AccountTier;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewAccountRequestDto {
    @NotNull(message = "User ID is required.")
    private String userId;

    @DecimalMin(value = "0.0", message = "Initial balance must be at least zero.")
    @DecimalMax(value = "200000000.0", message = "Initial balance cannot exceed two hundred million.")
    @NotNull(message = "Please enter an initial balance.")
    private BigDecimal initialBalance;

    @NotNull(message = "Please select an account tier.")
    private AccountTier accountTier;
}

