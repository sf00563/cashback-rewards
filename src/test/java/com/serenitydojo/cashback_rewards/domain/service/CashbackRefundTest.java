package com.serenitydojo.cashback_rewards.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reclaiming cashback after refund")
public class CashbackRefundTest {
    private final CashbackRefunder cashbackRefunder = new CashbackRefunder();

    @ParameterizedTest
    @DisplayName("Refund reduces the balance, clamping at zero")
    @CsvSource({
            "5.00, 5.00, 0.00",   // fully refunded
            "5.00, 3.00, 2.00",   // partially refunded
            "1.00, 3.00, 0.00"    // clamps at zero
    })
    void reducesBalanceOnRefund(BigDecimal balance, BigDecimal refunded, BigDecimal expected) {
        assertThat(cashbackRefunder.refund(balance, refunded)).isEqualByComparingTo(expected);
    }
}
