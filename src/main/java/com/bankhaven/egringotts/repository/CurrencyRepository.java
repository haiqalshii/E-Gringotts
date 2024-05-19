package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {

}
