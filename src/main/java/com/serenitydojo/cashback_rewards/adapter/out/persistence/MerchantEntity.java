package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchants")
class MerchantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean partner;

    protected MerchantEntity() {
    }

    MerchantEntity(boolean partner) {
        this.partner = partner;
    }

    Long getId() {
        return id;
    }

    boolean isPartner() {
        return partner;
    }
}
