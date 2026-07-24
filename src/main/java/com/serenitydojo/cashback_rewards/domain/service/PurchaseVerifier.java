package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.exception.IneligibleTransactionException;
import com.serenitydojo.cashback_rewards.domain.model.CardState;
import com.serenitydojo.cashback_rewards.domain.model.TransactionStatus;
import com.serenitydojo.cashback_rewards.domain.model.TransactionType;

public class PurchaseVerifier {
    public void verify(TransactionType type, TransactionStatus status, CardState cardState) {
        switch (type) {
            case PURCHASE -> {
            }
            case REFUND -> throw new IneligibleTransactionException("Invalid: transaction type refund");
            case FEE -> throw new IneligibleTransactionException("Invalid: transaction type fee");
        }

        switch (status) {
            case POSTED -> {
            }
            case PENDING -> throw new IneligibleTransactionException("Invalid: transaction status pending");
        }

        switch (cardState) {
            case ACTIVE -> {
            }
            case FROZEN -> throw new IneligibleTransactionException("Invalid: card state frozen");
            case CANCELLED -> throw new IneligibleTransactionException("Invalid: card state cancelled");
        }
    }
}
