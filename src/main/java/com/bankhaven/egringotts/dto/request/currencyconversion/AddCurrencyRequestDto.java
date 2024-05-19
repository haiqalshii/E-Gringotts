package com.bankhaven.egringotts.dto.request.currencyconversion;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AddCurrencyRequestDto {
    private String currencyCode;
}
