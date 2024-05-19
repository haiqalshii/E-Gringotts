package com.bankhaven.egringotts.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteRequestDto {
    @NotNull(message = "User ID cannot be null.")
    private String userId;
}
