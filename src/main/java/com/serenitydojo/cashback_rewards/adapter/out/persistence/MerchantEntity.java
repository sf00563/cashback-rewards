package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "merchants")
class MerchantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal cashbackRate;

    protected MerchantEntity() {
    }

    MerchantEntity(BigDecimal cashbackRate) {
        this.cashbackRate = cashbackRate;
    }

    Long getId() {
        return id;
    }

    BigDecimal getCashbackRate() {
        return cashbackRate;
    }
}
