package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
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
@Import(JpaCustomerRepository.class)
@DisplayName("Storing and retrieving customers")
class JpaCustomerRepositoryTest {

    @Autowired
    private CustomerRepository customers;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("The one where a saved customer is found by id carrying its rewards balance")
    void findsASavedCustomerByIdWithItsBalance() {
        Long id = entityManager.persistFlushFind(new CustomerEntity(new BigDecimal("10.00"))).getId();

        Optional<Customer> found = customers.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().balance()).isEqualByComparingTo("10.00");
    }
}
