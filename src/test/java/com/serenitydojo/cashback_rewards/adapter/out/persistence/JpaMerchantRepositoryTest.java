package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaMerchantRepository.class)
@DisplayName("Storing and retrieving merchants")
class JpaMerchantRepositoryTest {

    @Autowired
    private MerchantRepository merchants;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("The one where a saved merchant is found by id carrying its partner status")
    void findsASavedMerchantByIdWithItsPartnerStatus() {
        Long id = entityManager.persistFlushFind(new MerchantEntity(true)).getId();

        Optional<Merchant> found = merchants.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().partner()).isTrue();
    }
}
