package com.serenitydojo.cashback_rewards.domain.exception;

public class IneligibleTransactionException extends RuntimeException {
    public IneligibleTransactionException(String message) {
        super(message);
    }
}
