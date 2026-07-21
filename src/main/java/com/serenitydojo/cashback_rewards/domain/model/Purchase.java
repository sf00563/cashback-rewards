package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;

public record Purchase(Long purchaseId, CashbackRate cashbackRate, long customerId, BigDecimal purchaseAmount, BigDecimal totalRefunded) {

    public Purchase(CashbackRate cashbackRate, long customerId, BigDecimal purchaseAmount, BigDecimal totalRefunded) {
        this(null, cashbackRate, customerId, purchaseAmount, totalRefunded);
    }
}
