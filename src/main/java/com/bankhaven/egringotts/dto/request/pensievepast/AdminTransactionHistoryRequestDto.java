package com.bankhaven.egringotts.dto.request.pensievepast;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminTransactionHistoryRequestDto {
    private String accountId;
    private String sortBy;
    private String filterByCategory;
    private String filterByType;

    // Getters and setters
}
