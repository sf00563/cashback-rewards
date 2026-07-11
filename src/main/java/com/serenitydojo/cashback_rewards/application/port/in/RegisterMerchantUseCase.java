package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;

public interface RegisterMerchantUseCase {
    long register(CashbackRate cashbackRate);
}
