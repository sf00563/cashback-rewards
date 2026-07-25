package com.serenitydojo.cashback_rewards.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Resolving the cashback rate from a transaction's MCC")
class MerchantCategoryTest {

    @DisplayName("Should resolve the cashback rate configured for the transaction's MCC")
    @ParameterizedTest(name = "The one where MCC {0} resolves to a {1}% rate")
    @CsvSource({
            "5411, 2",
            "5541, 1",
            "5999, 0.5",
    })
    void resolvesTheRateForAnMcc(int mcc, String expectedRatePercentage) {
        assertThat(MerchantCategory.forMcc(mcc).rate().percentage())
                .isEqualByComparingTo(expectedRatePercentage);
    }
}
