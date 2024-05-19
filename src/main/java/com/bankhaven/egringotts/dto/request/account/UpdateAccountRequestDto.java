package com.bankhaven.egringotts.dto.request.account;

import com.bankhaven.egringotts.model.enums.AccountTier;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAccountRequestDto {
    private String accountId;
    private AccountTier accountTier;
    private BigDecimal balance;
}
