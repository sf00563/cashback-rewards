package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Service;

@Service
public class RegisterMerchantService implements RegisterMerchantUseCase {

    private final MerchantRepository merchants;

    public RegisterMerchantService(MerchantRepository merchants) {
        this.merchants = merchants;
    }

    public long register(boolean partner) {
        return merchants.save(new Merchant(partner));
    }
}
