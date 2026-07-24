package com.serenitydojo.cashback_rewards.domain.model;

public record TransactionDetails(TransactionType transactionType, TransactionStatus transactionStatus, CardState cardState) {
}
