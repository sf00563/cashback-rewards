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

    @Column(nullable = false)
    private BigDecimal purchaseAmount;

    @Column(nullable = false)
    private BigDecimal totalRefunded;

    protected PurchaseEntity() {
    }

    public PurchaseEntity(BigDecimal cashbackRate, Long customerId, BigDecimal purchaseAmount, BigDecimal totalRefunded) {
        this(null, cashbackRate, customerId, purchaseAmount, totalRefunded);
    }

    public PurchaseEntity(Long id, BigDecimal cashbackRate, Long customerId, BigDecimal purchaseAmount, BigDecimal totalRefunded) {
        this.id = id;
        this.cashbackRate = cashbackRate;
        this.customerId = customerId;
        this.purchaseAmount = purchaseAmount;
        this.totalRefunded = totalRefunded;
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

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public BigDecimal getTotalRefunded() {
        return totalRefunded;
    }
}
