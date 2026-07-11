package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.PurchaseRepository;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaPurchaseRepository.class)
@DisplayName("Storing and retrieving purchases")
public class JpaPurchaseRepositoryTest {

    @Autowired
    PurchaseRepository purchases;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("The one where a saved purchase is found by id carrying its cashback rate and customer id")
    void findsASavedPurchaseByIdWithItsCashbackRateAndCustomerId() {
        Long id = entityManager.persistFlushFind(new PurchaseEntity(new BigDecimal("5.00"), 20L)).getId();

        Optional<Purchase> found = purchases.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().cashbackRate().percentage()).isEqualByComparingTo("5");
        assertThat(found.get().customerId()).isEqualTo(20L);
    }
}
