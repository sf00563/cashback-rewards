package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.in.RefundPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.RefundReceipt;
import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownPurchaseException;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CashbackRefunder;
import com.serenitydojo.cashback_rewards.domain.service.PurchaseRefunder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class RefundPurchaseService implements RefundPurchaseUseCase {
    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final CashbackRefunder cashbackRefunder = new CashbackRefunder();
    private final CashbackCalculator cashbackCalculator = new CashbackCalculator();
    private final PurchaseRefunder purchaseRefunder = new PurchaseRefunder();

    public RefundPurchaseService(
            CustomerRepository customerRepository,
            PurchaseRepository purchaseRepository
    ) {
        this.customerRepository = customerRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    @Transactional
    public RefundReceipt refundPurchase(long purchaseId, BigDecimal refundAmount) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new UnknownPurchaseException("Unknown purchase: " + purchaseId));
        Customer customer = customerRepository.findById(purchase.customerId())
                .orElseThrow(() -> new UnknownCustomerException("Unknown customer: " + purchase.customerId()));

        BigDecimal customerCurrentBalance = customer.balance();
        BigDecimal refundedCashback = cashbackCalculator.cashbackFor(purchase.cashbackRate(), refundAmount);
        BigDecimal newCashbackBalance = cashbackRefunder.refund(customerCurrentBalance, refundedCashback);
        BigDecimal refundedTotal = purchaseRefunder.refund(purchase, refundAmount);
        customerRepository.updateBalance(purchase.customerId(), newCashbackBalance);
        purchaseRepository.save(new Purchase(purchase.purchaseId(), purchase.cashbackRate(), purchase.customerId(), purchase.purchaseAmount(), refundedTotal));

        return new RefundReceipt(refundedTotal, refundedCashback);
    }
}
