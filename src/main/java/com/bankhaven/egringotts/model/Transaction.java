package com.bankhaven.egringotts.model;

import com.bankhaven.egringotts.model.enums.TransactionCategory;
import com.bankhaven.egringotts.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "sender_account_id")
    private Account senderAccount;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "receiver_account_id")
    private Account receiverAccount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @NonNull
    private TransactionCategory transactionCategory;

    private String description;

    public Transaction(TransactionType transactionType, BigDecimal amount, LocalDateTime date, String description) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }


}