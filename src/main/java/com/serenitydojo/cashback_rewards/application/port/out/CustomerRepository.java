package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Customer;

import java.util.Optional;

public interface CustomerRepository {

    long save(Customer customer);

    Optional<Customer> findById(long id);
}
