package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByUserId(String userId);

    @Query("SELECT a FROM Account a")
    List<Account> findAllAccounts();

    Optional<Account> findAccountByAccountNumber(String accountNumber);

    @Query("SELECT a.id FROM Account a WHERE a.accountNumber = :accountNumber")
    String findIdByAccountNumber(String accountNumber);
}