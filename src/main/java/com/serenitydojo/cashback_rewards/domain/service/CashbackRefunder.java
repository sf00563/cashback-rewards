package com.serenitydojo.cashback_rewards.domain.service;

import java.math.BigDecimal;

public class CashbackRefunder {

    public BigDecimal refund(BigDecimal currentCashbackValue, BigDecimal refundedCashBack) {
        BigDecimal newValue = currentCashbackValue.subtract(refundedCashBack);
        if (newValue.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return newValue;
    }
}
