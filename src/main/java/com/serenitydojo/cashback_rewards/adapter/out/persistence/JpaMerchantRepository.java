package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class JpaMerchantRepository implements MerchantRepository {

    private final MerchantJpaRepository merchants;

    JpaMerchantRepository(MerchantJpaRepository merchants) {
        this.merchants = merchants;
    }

    @Override
    public long save(Merchant merchant) {
        return merchants.save(new MerchantEntity(merchant.partner())).getId();
    }

    @Override
    public Optional<Merchant> findById(long id) {
        return merchants.findById(id)
                .map(entity -> new Merchant(entity.isPartner()));
    }
}
