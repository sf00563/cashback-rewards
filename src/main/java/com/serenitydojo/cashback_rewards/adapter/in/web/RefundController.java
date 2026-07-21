package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serenitydojo.cashback_rewards.application.port.in.RefundPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.RefundReceipt;
import com.serenitydojo.cashback_rewards.domain.exception.PurchaseAmountExceededException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownPurchaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/refunds")
class RefundController {

    private final RefundPurchaseUseCase refundPurchaseUseCase;

    RefundController(RefundPurchaseUseCase refundPurchaseUseCase) {
        this.refundPurchaseUseCase = refundPurchaseUseCase;
    }

    @PostMapping
    ResponseEntity<RefundResponse> record(@RequestBody RefundRequest request) {
        RefundReceipt refundReceipt = refundPurchaseUseCase.refundPurchase(request.purchaseId(), request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(new RefundResponse(refundReceipt.totalCashbackRefunded()));
    }

    @ExceptionHandler({UnknownPurchaseException.class})
    ResponseEntity<Void> onUnknownPurchaseReference() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(PurchaseAmountExceededException.class)
    ResponseEntity<Void> onRefundExceedingPurchaseAmount() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    record RefundRequest(long purchaseId, BigDecimal amount) {
    }

    record RefundResponse(@JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal cashbackReversed) {
    }
}
