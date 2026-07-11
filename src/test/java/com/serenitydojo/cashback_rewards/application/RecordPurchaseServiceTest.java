package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseReceipt;
import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownMerchantException;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recording a purchase")
class RecordPurchaseServiceTest {

    @Mock
    private CustomerRepository customers;

    @Mock
    private MerchantRepository merchants;

    @Mock
    private PurchaseRepository purchases;

    @Test
    @DisplayName("The one where the cashback is computed from the purchase merchant's configured rate")
    void computesCashbackFromTheMerchantsConfiguredRate() {
        given(customers.findById(7L))
                .willReturn(Optional.of(new Customer(new BigDecimal("0.00"))));
        given(merchants.findById(42L))
                .willReturn(Optional.of(new Merchant(new CashbackRate(new BigDecimal("5")))));

        RecordPurchaseService service = new RecordPurchaseService(customers, merchants, purchases);

        PurchaseReceipt receipt = service.recordPurchase(7L, 42L, new BigDecimal("100.00"));

        assertThat(receipt.cashback()).isEqualByComparingTo("5.00");
    }

    @Test
    @DisplayName("The one where the purchase is saved against its customer with the merchant's rate, and its id is returned")
    void savesTheRecordedPurchaseAndReturnsItsId() {
        given(customers.findById(7L))
                .willReturn(Optional.of(new Customer(new BigDecimal("0.00"))));
        given(merchants.findById(42L))
                .willReturn(Optional.of(new Merchant(new CashbackRate(new BigDecimal("5")))));
        given(purchases.save(any(Purchase.class))).willReturn(99L);

        RecordPurchaseService service = new RecordPurchaseService(customers, merchants, purchases);

        PurchaseReceipt receipt = service.recordPurchase(7L, 42L, new BigDecimal("100.00"));

        assertThat(receipt.purchaseId()).isEqualTo(99L);
        verify(purchases).save(new Purchase(new CashbackRate(new BigDecimal("5")), 7L));
    }

    @Test
    @DisplayName("The one where the earned cashback is credited to the customer's running balance")
    void creditsEarnedCashbackToTheCustomersRunningBalance() {
        given(customers.findById(7L))
                .willReturn(Optional.of(new Customer(new BigDecimal("10.00"))));
        given(merchants.findById(42L))
                .willReturn(Optional.of(new Merchant(new CashbackRate(new BigDecimal("5")))));

        RecordPurchaseService service = new RecordPurchaseService(customers, merchants, purchases);

        service.recordPurchase(7L, 42L, new BigDecimal("100.00")); // earns 5.00

        verify(customers).updateBalance(eq(7L),
                argThat(balance -> balance.compareTo(new BigDecimal("15.00")) == 0));
    }

    @Test
    @DisplayName("The one where the merchant is unknown -> the purchase is rejected")
    void rejectsAPurchaseForAnUnknownMerchant() {
        given(merchants.findById(999L)).willReturn(Optional.empty());

        RecordPurchaseService service = new RecordPurchaseService(customers, merchants, purchases);

        assertThatThrownBy(() -> service.recordPurchase(1L, 999L, new BigDecimal("100.00")))
                .isInstanceOf(UnknownMerchantException.class);
    }

    @Test
    @DisplayName("The one where the customer is unknown -> the purchase is rejected")
    void rejectsAPurchaseForAnUnknownCustomer() {
        given(merchants.findById(42L))
                .willReturn(Optional.of(new Merchant(new CashbackRate(new BigDecimal("5")))));
        given(customers.findById(999L)).willReturn(Optional.empty());

        RecordPurchaseService service = new RecordPurchaseService(customers, merchants, purchases);

        assertThatThrownBy(() -> service.recordPurchase(999L, 42L, new BigDecimal("100.00")))
                .isInstanceOf(UnknownCustomerException.class);
    }
}
