package com.bankhaven.egringotts.dto.model;

import com.bankhaven.egringotts.model.enums.AccountTier;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDto {
    private String id;
    private UserDto userDto;
    private String accountNumber;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private AccountTier accountTier;
}
