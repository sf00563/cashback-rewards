package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.exception.IneligibleTransactionException;
import com.serenitydojo.cashback_rewards.domain.model.CardState;
import com.serenitydojo.cashback_rewards.domain.model.TransactionStatus;
import com.serenitydojo.cashback_rewards.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Verifying a purchase can receive cashback")
class PurchaseVerifierTest {

    private final PurchaseVerifier purchaseVerifier = new PurchaseVerifier();

    @DisplayName("Invalid state does not receive cashback")
    @ParameterizedTest(name = "The one where transaction type={0} transaction status={1} cardstate={2} is ineligible")
    @CsvSource({
            "PURCHASE, PENDING, ACTIVE, Invalid: transaction status pending",
            "PURCHASE, POSTED, FROZEN, Invalid: card state frozen",
            "PURCHASE, POSTED, CANCELLED, Invalid: card state cancelled",
            "REFUND, POSTED, ACTIVE, Invalid: transaction type refund",
            "FEE, POSTED, ACTIVE, Invalid: transaction type fee"
    })
    void invalidStateThrowsError(TransactionType type, TransactionStatus status, CardState cardState, String errorMessage) {
        assertThatThrownBy(() -> purchaseVerifier.verify(type, status, cardState))
                .isInstanceOf(IneligibleTransactionException.class)
                .hasMessage(errorMessage);
    }

    @DisplayName("Valid state does not throw error")
    @Test
    void validStatePasses() {
        assertThatCode(() -> purchaseVerifier.verify(TransactionType.PURCHASE, TransactionStatus.POSTED, CardState.ACTIVE))
                .doesNotThrowAnyException();
    }
}
