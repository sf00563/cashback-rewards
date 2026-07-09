package com.serenitydojo.cashback_rewards.domain.exception;

public class UnknownPurchaseException extends RuntimeException {
    public UnknownPurchaseException(String message) {
        super(message);
    }
}
