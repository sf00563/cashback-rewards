package com.serenitydojo.cashback_rewards.domain.exception;

public class UnknownCustomerException extends RuntimeException {

    public UnknownCustomerException(String message) {
        super(message);
    }
}
