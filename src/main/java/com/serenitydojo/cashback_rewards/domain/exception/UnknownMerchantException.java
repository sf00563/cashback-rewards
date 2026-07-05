package com.serenitydojo.cashback_rewards.domain.exception;

public class UnknownMerchantException extends RuntimeException {

    public UnknownMerchantException(String message) {
        super(message);
    }
}
