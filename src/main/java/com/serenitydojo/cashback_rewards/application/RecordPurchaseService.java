package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseReceipt;
import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownMerchantException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCreditor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RecordPurchaseService implements RecordPurchaseUseCase {

    private final CustomerRepository customers;
    private final MerchantRepository merchants;
    private final PurchaseRepository purchases;
    private final CashbackCalculator calculator = new CashbackCalculator();
    private final CashbackCreditor creditor = new CashbackCreditor();

    public RecordPurchaseService(CustomerRepository customers, MerchantRepository merchants, PurchaseRepository purchases) {
        this.customers = customers;
        this.merchants = merchants;
        this.purchases = purchases;
    }

    @Transactional
    public PurchaseReceipt recordPurchase(long customerId, long merchantId, BigDecimal amount) {
        Merchant merchant = merchants.findById(merchantId)
                .orElseThrow(() -> new UnknownMerchantException("Unknown merchant: " + merchantId));
        Customer customer = customers.findById(customerId)
                .orElseThrow(() -> new UnknownCustomerException("Unknown customer: " + customerId));

        BigDecimal cashback = calculator.cashbackFor(merchant.cashbackRate(), amount);
        BigDecimal newBalance = creditor.credit(customer.balance(), cashback);
        long purchaseId = purchases.save(new Purchase(new CashbackRate(merchant.cashbackRate().percentage()), customerId, amount, new BigDecimal("0")));
        customers.updateBalance(customerId, newBalance);

        return new PurchaseReceipt(purchaseId, cashback);
    }
}
