package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.RegisterCustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
class CustomerController {

    private final RegisterCustomerService registerCustomerService;

    CustomerController(RegisterCustomerService registerCustomerService) {
        this.registerCustomerService = registerCustomerService;
    }

    @PostMapping
    ResponseEntity<IdResponse> create() {
        long id = registerCustomerService.register();
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }
}
