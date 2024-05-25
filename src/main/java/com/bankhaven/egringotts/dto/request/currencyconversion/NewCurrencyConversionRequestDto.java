package com.bankhaven.egringotts.dto.request.currencyconversion;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class NewCurrencyConversionRequestDto {
    private String accountNumber;
    private String fromCurrency;
    private String toCurrency;
    private double amount;
}

