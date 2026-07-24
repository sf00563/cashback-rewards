package com.serenitydojo.cashback_rewards.domain.model;

import com.serenitydojo.cashback_rewards.domain.exception.MalformedTransactionException;

import java.util.Arrays;

public enum CardState {
    ACTIVE,
    FROZEN,
    CANCELLED;

    public static CardState fromValue(String value) {
        return Arrays.stream(values())
                .filter(cardState -> cardState.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MalformedTransactionException("Unrecognised card state: " + value));
    }
}
