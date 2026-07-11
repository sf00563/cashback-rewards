package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownPurchaseException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Refunding a purchase")
public class RefundPurchaseServiceTest {
    @Mock
    private CustomerRepository customers;
    @Mock
    private PurchaseRepository purchases;

    @Test
    @DisplayName("The one where the earned cashback is refunded and so the customer's running balance is reduced")
    void reclaimsEarnedCashbackFromTheCustomersRunningBalance() {
        given(customers.findById(7L))
                .willReturn(Optional.of(new Customer(new BigDecimal("10.00"))));
        given(purchases.findById(20L))
                .willReturn(Optional.of(new Purchase(new CashbackRate(new BigDecimal("5.00")), 7L)));

        RefundPurchaseService service = new RefundPurchaseService(customers, purchases);

        BigDecimal refundedCashback = service.refundPurchase(20L, new BigDecimal("40.00"));

        assertThat(refundedCashback).isEqualByComparingTo("2.00");
        verify(customers).updateBalance(eq(7L),
                argThat(balance -> balance.compareTo(new BigDecimal("8.00")) == 0));
    }

    @Test
    @DisplayName("The one where a customer id is not valid so cashback refunded is aborted")
    void rejectsRefundForUnknownCustomer() {
        given(purchases.findById(20L))
                .willReturn(Optional.of(new Purchase(new CashbackRate(new BigDecimal("5.00")), 7L)));
        given(customers.findById(7L))
                .willReturn(Optional.empty());

        RefundPurchaseService service = new RefundPurchaseService(customers, purchases);

        assertThatThrownBy(() -> service.refundPurchase(20L, new BigDecimal("40.00")))
                .isInstanceOf(UnknownCustomerException.class);
    }

    @Test
    @DisplayName("The one where a purchased id is not valid so cashback refunded is aborted")
    void rejectsRefundForUnknownPurchase() {
        given(purchases.findById(20L))
                .willReturn(Optional.empty());

        RefundPurchaseService service = new RefundPurchaseService(customers, purchases);

        assertThatThrownBy(() -> service.refundPurchase(20L, new BigDecimal("40.00")))
                .isInstanceOf(UnknownPurchaseException.class);
    }
}
