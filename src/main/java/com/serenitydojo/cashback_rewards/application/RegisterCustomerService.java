package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RegisterCustomerService {

    private static final BigDecimal ZERO_BALANCE = new BigDecimal("0.00");

    private final CustomerRepository customers;

    public RegisterCustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    public long register() {
        return customers.save(new Customer(ZERO_BALANCE));
    }
}
