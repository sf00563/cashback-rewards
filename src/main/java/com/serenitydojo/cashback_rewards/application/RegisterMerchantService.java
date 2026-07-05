package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Service;

@Service
public class RegisterMerchantService {

    private final MerchantRepository merchants;

    public RegisterMerchantService(MerchantRepository merchants) {
        this.merchants = merchants;
    }

    public long register(CashbackRate cashbackRate) {
        return merchants.save(new Merchant(cashbackRate));
    }
}
