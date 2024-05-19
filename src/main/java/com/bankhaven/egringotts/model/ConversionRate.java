package com.bankhaven.egringotts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conversion_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_currency", referencedColumnName = "code")
    private Currency fromCurrency;

    @ManyToOne
    @JoinColumn(name = "to_currency", referencedColumnName = "code")
    private Currency toCurrency;

    private double conversionRate;
    private double fee;

    public ConversionRate(Currency fromCurrency, Currency toCurrency, double conversionRate, double fee) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.conversionRate = conversionRate;
        this.fee = fee;
    }
}
