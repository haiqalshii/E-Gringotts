package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.id = :accountId OR t.receiverAccount.id = :accountId OR t.senderAccount IS NULL OR t.receiverAccount IS NULL")
    List<Transaction> findAllTransactionsByAccountId(@Param("accountId") String accountId);

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.accountNumber = :accountNumber OR t.receiverAccount.accountNumber = :accountNumber")
    List<Transaction> findAllTransactionsByAccountNumber(@Param("accountNumber") String accountNumber);

    List<Transaction> findAll();
}

