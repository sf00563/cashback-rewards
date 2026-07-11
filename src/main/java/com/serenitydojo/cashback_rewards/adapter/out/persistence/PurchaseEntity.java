package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchases")
class PurchaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal cashbackRate;

    @Column(nullable = false)
    private Long customerId;

    protected PurchaseEntity() {
    }

    public PurchaseEntity(BigDecimal cashbackRate, Long customerId) {
        this.cashbackRate = cashbackRate;
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCashbackRate() {
        return cashbackRate;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
