package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class RecordPurchaseService {

    private final MerchantRepository merchants;
    private final CashbackCalculator calculator = new CashbackCalculator();

    public RecordPurchaseService(MerchantRepository merchants) {
        this.merchants = merchants;
    }

    public BigDecimal recordPurchase(long merchantId, BigDecimal amount) {
        Merchant merchant = merchants.findById(merchantId)
                .orElseThrow(NoSuchElementException::new);
        return calculator.cashbackFor(merchant.cashbackRate(), amount);
    }
}
