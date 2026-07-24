package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.TransactionDetails;

import java.math.BigDecimal;

public interface RecordPurchaseUseCase {
    PurchaseReceipt recordPurchase(long customerId, long merchantId, BigDecimal amount, int mcc, TransactionDetails transactionDetails);
}
