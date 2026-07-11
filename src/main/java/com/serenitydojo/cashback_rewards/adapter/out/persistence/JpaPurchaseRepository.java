package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaPurchaseRepository implements PurchaseRepository {

    private final PurchaseJpaRepository repository;

    JpaPurchaseRepository(PurchaseJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Purchase> findById(long id) {
        return repository.findById(id)
                .map(entity -> new Purchase(new CashbackRate(entity.getCashbackRate()), entity.getCustomerId()));
    }

    @Override
    public long save(Purchase purchase) {
        return repository.save(new PurchaseEntity(purchase.cashbackRate().percentage(), purchase.customerId())).getId();
    }
}
