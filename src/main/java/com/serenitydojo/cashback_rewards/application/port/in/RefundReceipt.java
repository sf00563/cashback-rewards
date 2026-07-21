package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public record RefundReceipt(BigDecimal totalRefunded, BigDecimal totalCashbackRefunded) {
}
