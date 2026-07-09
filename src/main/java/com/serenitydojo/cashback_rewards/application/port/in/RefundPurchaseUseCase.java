package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public interface RefundPurchaseUseCase {
    BigDecimal refundPurchase(long customerId, long purchaseId, BigDecimal refundAmount);
}
