package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public interface RecordPurchaseUseCase {
    PurchaseReceipt recordPurchase(long customerId, long merchantId, BigDecimal amount);
}
