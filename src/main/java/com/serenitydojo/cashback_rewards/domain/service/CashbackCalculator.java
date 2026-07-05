package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashbackCalculator {

    private static final int MONEY_SCALE = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public BigDecimal cashbackFor(CashbackRate rate, BigDecimal purchaseAmount) {
        return purchaseAmount.multiply(rate.percentage())
                .divide(HUNDRED, MONEY_SCALE, RoundingMode.DOWN);
    }
}
