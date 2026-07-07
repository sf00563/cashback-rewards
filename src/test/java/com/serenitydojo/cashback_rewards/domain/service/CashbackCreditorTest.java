package com.serenitydojo.cashback_rewards.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Crediting cashback to a customer's rewards balance")
class CashbackCreditorTest {

    private final CashbackCreditor creditor = new CashbackCreditor();

    @Test
    @DisplayName("The one where crediting 5.00 to a 10.00 balance yields 15.00")
    void addsEarnedCashbackToTheExistingBalance() {
        BigDecimal newBalance = creditor.credit(new BigDecimal("10.00"), new BigDecimal("5.00"));

        assertThat(newBalance).isEqualByComparingTo("15.00");
    }
}
