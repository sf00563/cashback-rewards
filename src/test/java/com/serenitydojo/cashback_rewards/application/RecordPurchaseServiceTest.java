package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recording a purchase")
class RecordPurchaseServiceTest {

    @Mock
    private MerchantRepository merchants;

    @Test
    @DisplayName("The one where the cashback is computed from the purchase merchant's configured rate")
    void computesCashbackFromTheMerchantsConfiguredRate() {
        given(merchants.findById(42L))
                .willReturn(Optional.of(new Merchant(new CashbackRate(new BigDecimal("5")))));

        RecordPurchaseService service = new RecordPurchaseService(merchants);

        BigDecimal cashback = service.recordPurchase(42L, new BigDecimal("100.00"));

        assertThat(cashback).isEqualByComparingTo("5.00");
    }
}
