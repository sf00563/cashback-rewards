package com.serenitydojo.cashback_rewards.domain.exception;

public class MalformedTransactionException extends RuntimeException {

    public MalformedTransactionException(String message) {
        super(message);
    }
}
