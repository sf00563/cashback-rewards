package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serenitydojo.cashback_rewards.application.RecordPurchaseService;
import com.serenitydojo.cashback_rewards.domain.exception.InvalidPurchaseAmountException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownMerchantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/purchases")
class PurchaseController {

    private final RecordPurchaseService recordPurchaseService;

    PurchaseController(RecordPurchaseService recordPurchaseService) {
        this.recordPurchaseService = recordPurchaseService;
    }

    @PostMapping
    ResponseEntity<PurchaseResponse> record(@RequestBody PurchaseRequest request) {
        BigDecimal cashback = recordPurchaseService.recordPurchase(
                request.customerId(), request.merchantId(), request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PurchaseResponse(cashback));
    }

    @ExceptionHandler(InvalidPurchaseAmountException.class)
    ResponseEntity<Void> onInvalidPurchase() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({UnknownMerchantException.class, UnknownCustomerException.class})
    ResponseEntity<Void> onUnknownReference() {
        return ResponseEntity.notFound().build();
    }

    record PurchaseRequest(long customerId, long merchantId, BigDecimal amount) {
    }

    record PurchaseResponse(@JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal cashback) {
    }
}
