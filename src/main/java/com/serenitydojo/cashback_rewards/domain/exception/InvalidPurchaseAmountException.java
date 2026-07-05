package com.serenitydojo.cashback_rewards.domain.exception;

public class InvalidPurchaseAmountException extends RuntimeException {

    public InvalidPurchaseAmountException(String message) {
        super(message);
    }
}
