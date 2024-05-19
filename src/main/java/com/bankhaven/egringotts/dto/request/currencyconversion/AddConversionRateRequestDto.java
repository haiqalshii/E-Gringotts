package com.bankhaven.egringotts.dto.request.currencyconversion;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AddConversionRateRequestDto {
    private String fromCurrency;
    private String toCurrency;
    private double conversionRate;
    private double fee;
}
