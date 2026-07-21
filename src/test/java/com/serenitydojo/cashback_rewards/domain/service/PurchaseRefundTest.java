package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.exception.PurchaseAmountExceededException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("Applying refund to purchase")
public class PurchaseRefundTest {

    private final PurchaseRefunder purchaseRefunder = new PurchaseRefunder();

    @Test
    @DisplayName("Refund is not applied if it takes the cumulative refunds past the purchase amount")
    void refundNotAppliedAsPurchaseAmountExceeded() {
        Purchase purchase = new Purchase(
                new CashbackRate(BigDecimal.valueOf(5)),
                20L,
                new BigDecimal("30.00"),
                new BigDecimal("15.00"));

        assertThrows(PurchaseAmountExceededException.class, () -> {
            purchaseRefunder.refund(purchase, new BigDecimal("20.00"));
        });
    }
}
