package com.bankhaven.egringotts.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CardDto {
    private String cardNumber;
    private String cvv;
    private LocalDate expirationDate;
}
