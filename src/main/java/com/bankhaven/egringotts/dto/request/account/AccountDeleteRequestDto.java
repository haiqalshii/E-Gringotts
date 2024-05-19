package com.bankhaven.egringotts.dto.request.account;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDeleteRequestDto {
    @NotNull(message = "Account ID cannot be null.")
    private String accountId;
}
