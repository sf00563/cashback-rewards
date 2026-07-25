package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.MerchantCategory;

import java.math.BigDecimal;

public class CashbackRateResolver {

    private static final CashbackRate NONE = new CashbackRate(BigDecimal.ZERO);

    public CashbackRate rateFor(boolean partner, int mcc) {
        if (!partner) {
            return NONE;
        }
        return MerchantCategory.forMcc(mcc).rate();
    }
}
