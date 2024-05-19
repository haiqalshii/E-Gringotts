package com.bankhaven.egringotts.dto.model;

import com.bankhaven.egringotts.model.enums.TransactionCategory;
import com.bankhaven.egringotts.model.enums.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionCategory transactionCategory;
    private String description;
    @Nullable
    private TransactionUserDto senderUser;
    @Nullable
    private TransactionUserDto receiverUser;
}
