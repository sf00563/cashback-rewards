package com.serenitydojo.cashback_rewards.domain.model;

import com.serenitydojo.cashback_rewards.domain.exception.MalformedTransactionException;

import java.util.Arrays;

public enum TransactionType {
    PURCHASE,
    REFUND,
    FEE;

    public static TransactionType fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MalformedTransactionException("Unrecognised transaction type: " + value));
    }
}
