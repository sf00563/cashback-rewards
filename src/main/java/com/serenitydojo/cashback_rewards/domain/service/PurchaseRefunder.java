package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.exception.PurchaseAmountExceededException;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;

import java.math.BigDecimal;

public class PurchaseRefunder {

    public BigDecimal refund(Purchase purchase, BigDecimal refundAmount) {
        BigDecimal purchaseValue = purchase.purchaseAmount();
        BigDecimal currentRefundTotal = purchase.totalRefunded();

        if (currentRefundTotal.add(refundAmount).compareTo(purchaseValue) > 0) {
            throw new PurchaseAmountExceededException("refund amount: " + refundAmount + " exceeded purchase value: " + purchaseValue);
        }

        return currentRefundTotal.add(refundAmount);
    }
}
