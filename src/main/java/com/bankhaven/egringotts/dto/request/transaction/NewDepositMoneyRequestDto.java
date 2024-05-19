package com.bankhaven.egringotts.dto.request.transaction;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewDepositMoneyRequestDto {
    private String currentAccountNumber;
    @DecimalMin(value = "1.0", message = "Deposit amount must be at least one.")
    @DecimalMax(value = "200000000.0", message = "Deposit amount cannot exceed two hundred million.")
    @NotNull(message = "Please enter a deposit amount.")
    private BigDecimal amount;

    @Nullable
    private String description;
}
