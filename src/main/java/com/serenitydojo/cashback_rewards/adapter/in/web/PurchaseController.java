package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serenitydojo.cashback_rewards.application.port.in.PurchaseReceipt;
import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.IneligibleTransactionException;
import com.serenitydojo.cashback_rewards.domain.exception.InvalidPurchaseAmountException;
import com.serenitydojo.cashback_rewards.domain.exception.MalformedTransactionException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownMerchantException;
import com.serenitydojo.cashback_rewards.domain.model.CardState;
import com.serenitydojo.cashback_rewards.domain.model.TransactionDetails;
import com.serenitydojo.cashback_rewards.domain.model.TransactionStatus;
import com.serenitydojo.cashback_rewards.domain.model.TransactionType;
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

    private final RecordPurchaseUseCase recordPurchaseUseCase;

    PurchaseController(RecordPurchaseUseCase recordPurchaseUseCase) {
        this.recordPurchaseUseCase = recordPurchaseUseCase;
    }

    @PostMapping
    ResponseEntity<PurchaseResponse> record(@RequestBody PurchaseRequest request) {
        PurchaseReceipt purchaseReceipt = recordPurchaseUseCase.recordPurchase(
                request.customerId(),
                request.merchantId(),
                request.amount(),
                request.mcc(),
                new TransactionDetails(
                        TransactionType.fromValue(request.type()),
                        TransactionStatus.fromValue(request.status()),
                        CardState.fromValue(request.cardState())
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new PurchaseResponse(purchaseReceipt.cashback(), purchaseReceipt.purchaseId()));
    }

    @ExceptionHandler({InvalidPurchaseAmountException.class, MalformedTransactionException.class})
    ResponseEntity<Void> onInvalidPurchase() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({UnknownMerchantException.class, UnknownCustomerException.class})
    ResponseEntity<Void> onUnknownReference() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({IneligibleTransactionException.class})
    ResponseEntity<Void> onIneligibleTransaction() { return ResponseEntity.status(HttpStatus.CONFLICT).build();}

    record PurchaseRequest(long customerId, long merchantId, BigDecimal amount, int mcc, String type, String status, String cardState) {
    }

    record PurchaseResponse(@JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal cashback, long id) {
    }
}
