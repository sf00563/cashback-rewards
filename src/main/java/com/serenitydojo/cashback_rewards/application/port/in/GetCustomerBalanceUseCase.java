package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public interface GetCustomerBalanceUseCase {

    BigDecimal getBalance(long customerId);
}
