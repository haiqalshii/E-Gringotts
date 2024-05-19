package com.bankhaven.egringotts.model;

import com.bankhaven.egringotts.model.enums.AccountTier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    private String accountNumber;

    private BigDecimal balance;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private AccountTier accountTier;


    public Account(String accountNumber, BigDecimal balance, LocalDateTime createdAt, AccountTier accountTier) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = createdAt;
        this.accountTier = accountTier;
    }
}
