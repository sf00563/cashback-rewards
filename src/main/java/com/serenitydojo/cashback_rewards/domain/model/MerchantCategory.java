package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;

public enum MerchantCategory {

    GROCERIES(new BigDecimal("2")),
    FUEL(new BigDecimal("1")),
    OTHER(new BigDecimal("0.5"));

    private final CashbackRate rate;

    MerchantCategory(BigDecimal percentage) {
        this.rate = new CashbackRate(percentage);
    }

    public CashbackRate rate() {
        return rate;
    }

    public static MerchantCategory forMcc(int mcc) {
        return switch (mcc) {
            case 5411 -> GROCERIES;
            case 5541 -> FUEL;
            default -> OTHER;
        };
    }
}
