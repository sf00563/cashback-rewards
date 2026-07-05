package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;

import java.util.Optional;

public interface MerchantRepository {

    long save(Merchant merchant);

    Optional<Merchant> findById(long id);
}
