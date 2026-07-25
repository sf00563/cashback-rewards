package com.serenitydojo.cashback_rewards.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Resolving the effective cashback rate for a purchase")
class CashbackRateResolverTest {

    private final CashbackRateResolver resolver = new CashbackRateResolver();

    @DisplayName("Should gate the cashback rate to 0% at a non-partner merchant, regardless of MCC")
    @ParameterizedTest(name = "The one where a partner={0} merchant with grocery MCC 5411 resolves to a {1}% rate")
    @CsvSource({
            "true, 2",
            "false, 0",
    })
    void gatesTheRateToZeroForNonPartners(boolean partner, String expectedRatePercentage) {
        assertThat(resolver.rateFor(partner, 5411).percentage())
                .isEqualByComparingTo(expectedRatePercentage);
    }
}
