package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal balance;

    protected CustomerEntity() {
    }

    CustomerEntity(BigDecimal balance) {
        this.balance = balance;
    }

    Long getId() {
        return id;
    }

    BigDecimal getBalance() {
        return balance;
    }
}
