package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serenitydojo.cashback_rewards.application.RegisterCustomerService;
import com.serenitydojo.cashback_rewards.application.port.in.GetCustomerBalanceUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.UnknownCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/customers")
class CustomerController {

    private final RegisterCustomerService registerCustomerService;
    private final GetCustomerBalanceUseCase getCustomerBalance;

    CustomerController(RegisterCustomerService registerCustomerService,
                       GetCustomerBalanceUseCase getCustomerBalance) {
        this.registerCustomerService = registerCustomerService;
        this.getCustomerBalance = getCustomerBalance;
    }

    @PostMapping
    ResponseEntity<IdResponse> create() {
        long id = registerCustomerService.register();
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @GetMapping("/{id}")
    CustomerBalanceResponse balance(@PathVariable long id) {
        return new CustomerBalanceResponse(getCustomerBalance.getBalance(id));
    }

    @ExceptionHandler(UnknownCustomerException.class)
    ResponseEntity<Void> onUnknownCustomer() {
        return ResponseEntity.notFound().build();
    }

    record CustomerBalanceResponse(@JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal balance) {
    }
}
