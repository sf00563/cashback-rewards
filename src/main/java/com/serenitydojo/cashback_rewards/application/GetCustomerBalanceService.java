package com.serenitydojo.cashback_rewards.application;

import com.serenitydojo.cashback_rewards.application.port.in.GetCustomerBalanceUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CustomerRepository;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GetCustomerBalanceService implements GetCustomerBalanceUseCase {

    private final CustomerRepository customers;

    public GetCustomerBalanceService(CustomerRepository customers) {
        this.customers = customers;
    }

    @Override
    public BigDecimal getBalance(long customerId) {
        Customer customer = customers.findById(customerId)
                .orElseThrow(() -> new UnknownCustomerException("Unknown customer: " + customerId));
        return customer.balance();
    }
}
