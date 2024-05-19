package com.bankhaven.egringotts.dto.request.transaction;

import com.bankhaven.egringotts.model.enums.TransactionCategory;
import com.bankhaven.egringotts.model.enums.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewMoneyTransferRequestDto {

    private String senderAccountNumber;
    @Size(min = 12, max = 12, message = "Account number must be 12 digits long.")
    @NotBlank(message = "Please enter a valid account number.")
    @Pattern(regexp = "^[0-9]+$", message = "Account number can only contain digits.")
    private String receiverAccountNumber;

    @DecimalMin(value = "1.0", message = "Transfer amount must be at least one.")
    @DecimalMax(value = "200000000.0", message = "Transfer amount cannot exceed two hundred million.")
    @NotNull(message = "Please enter a transfer amount.")
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionCategory transactionCategory;

    @Nullable
    private String description;
}

