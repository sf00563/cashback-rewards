package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownMerchantException;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RecordPurchaseService {

    private final CustomerRepository customers;
    private final MerchantRepository merchants;
    private final CashbackCalculator calculator = new CashbackCalculator();

    public RecordPurchaseService(CustomerRepository customers, MerchantRepository merchants) {
        this.customers = customers;
        this.merchants = merchants;
    }

    public BigDecimal recordPurchase(long customerId, long merchantId, BigDecimal amount) {
        Merchant merchant = merchants.findById(merchantId)
                .orElseThrow(() -> new UnknownMerchantException("Unknown merchant: " + merchantId));
        customers.findById(customerId)
                .orElseThrow(() -> new UnknownCustomerException("Unknown customer: " + customerId));
        return calculator.cashbackFor(merchant.cashbackRate(), amount);
    }
}
