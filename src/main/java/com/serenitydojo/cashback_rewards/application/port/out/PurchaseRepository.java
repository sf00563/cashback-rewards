package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Purchase;

import java.util.Optional;

public interface PurchaseRepository {
    Optional<Purchase> findById(long id);
    long save(Purchase purchase);
}
