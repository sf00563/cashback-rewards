package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class JpaCustomerRepository implements CustomerRepository {

    private final CustomerJpaRepository customers;

    JpaCustomerRepository(CustomerJpaRepository customers) {
        this.customers = customers;
    }

    @Override
    public long save(Customer customer) {
        return customers.save(new CustomerEntity(customer.balance())).getId();
    }

    @Override
    public Optional<Customer> findById(long id) {
        return customers.findById(id)
                .map(entity -> new Customer(entity.getBalance()));
    }
}
