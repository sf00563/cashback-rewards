package com.serenitydojo.cashback_rewards.domain.service;

import java.math.BigDecimal;

public class CashbackCreditor {

    public BigDecimal credit(BigDecimal balance, BigDecimal cashback) {
        return balance.add(cashback);
    }
}
