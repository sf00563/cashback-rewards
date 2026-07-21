package com.serenitydojo.cashback_rewards.domain.exception;

public class PurchaseAmountExceededException extends RuntimeException {
    public PurchaseAmountExceededException(String message) {
        super(message);
    }
}
