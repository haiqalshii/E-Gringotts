package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.ConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversionRateRepository extends JpaRepository<ConversionRate, Long> {

    @Query("SELECT cr FROM ConversionRate cr WHERE cr.fromCurrency.code = :currencyCode")
    List<ConversionRate> findByFromCurrency(@Param("currencyCode") String currencyCode);
}
