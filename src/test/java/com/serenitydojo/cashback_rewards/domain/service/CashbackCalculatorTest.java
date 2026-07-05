package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.exception.InvalidPurchaseAmountException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Calculating cashback for a purchase")
class CashbackCalculatorTest {

    private final CashbackCalculator calculator = new CashbackCalculator();

    @DisplayName("Should apply the cashback rate configured for the purchase's merchant")
    @ParameterizedTest(name = "The one where a {0}% merchant applies its own rate to 100.00, yielding {1}")
    @CsvSource({
            "5, 5.00",
            "2, 2.00",
    })
    void appliesEachMerchantsOwnRateToTheSamePurchaseAmount(String rate, String expectedCashback) {
        CashbackRate cashbackRate = new CashbackRate(new BigDecimal(rate));

        assertThat(calculator.cashbackFor(cashbackRate, new BigDecimal("100.00")))
                .isEqualByComparingTo(expectedCashback);
    }

    @DisplayName("Should calculate cashback as the merchant's percentage applied to the amount, rounded down to 2 decimals")
    @ParameterizedTest(name = "The one where a 5% merchant on {0} yields {1}")
    @CsvSource({
            "33.33, 1.66",
            "0.01,  0.00",
    })
    void roundsCashbackDownToTwoDecimals(String amount, String expectedCashback) {
        CashbackRate fivePercent = new CashbackRate(new BigDecimal("5"));

        assertThat(calculator.cashbackFor(fivePercent, new BigDecimal(amount)))
                .isEqualByComparingTo(expectedCashback);
    }

    @DisplayName("Should reject a purchase with a non-positive amount")
    @ParameterizedTest(name = "The one where an amount of {0} is rejected")
    @CsvSource({
            "0.00",
            "-10.00",
    })
    void aNonPositiveAmountIsRejected(String amount) {
        CashbackRate fivePercent = new CashbackRate(new BigDecimal("5"));

        assertThatThrownBy(() -> calculator.cashbackFor(fivePercent, new BigDecimal(amount)))
                .isInstanceOf(InvalidPurchaseAmountException.class);
    }

    @Test
    @DisplayName("The one where a null amount is rejected")
    void aNullAmountIsRejected() {
        CashbackRate fivePercent = new CashbackRate(new BigDecimal("5"));

        assertThatThrownBy(() -> calculator.cashbackFor(fivePercent, null))
                .isInstanceOf(InvalidPurchaseAmountException.class);
    }
}
