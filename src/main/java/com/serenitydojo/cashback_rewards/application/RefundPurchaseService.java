package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.in.RefundPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownPurchaseException;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CashbackRefunder;
import java.math.BigDecimal;

public class RefundPurchaseService implements RefundPurchaseUseCase {
    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final CashbackRefunder cashbackRefunder = new CashbackRefunder();
    private final CashbackCalculator cashbackCalculator = new CashbackCalculator();

    public RefundPurchaseService(
            CustomerRepository customerRepository,
            PurchaseRepository purchaseRepository
    ) {
        this.customerRepository = customerRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public BigDecimal refundPurchase(long customerId, long purchaseId, BigDecimal refundAmount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new UnknownCustomerException("Unknown customer: " + customerId));
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new UnknownPurchaseException("Unknown purchase: " + purchaseId));

        BigDecimal customerCurrentBalance = customer.balance();
        BigDecimal refundedCashback = cashbackCalculator.cashbackFor(purchase.cashbackRate(), refundAmount);
        BigDecimal newBalance = cashbackRefunder.refund(customerCurrentBalance, refundedCashback);
        customerRepository.updateBalance(customerId, newBalance);

        return newBalance;
    }
}
