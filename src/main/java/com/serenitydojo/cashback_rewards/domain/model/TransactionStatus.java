package com.serenitydojo.cashback_rewards.domain.model;

import com.serenitydojo.cashback_rewards.domain.exception.MalformedTransactionException;

import java.util.Arrays;

public enum TransactionStatus {
    POSTED,
    PENDING;

    public static TransactionStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MalformedTransactionException("Unrecognised transaction status: " + value));
    }
}
