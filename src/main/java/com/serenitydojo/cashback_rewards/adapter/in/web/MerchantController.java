package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchants")
class MerchantController {

    private final RegisterMerchantUseCase registerMerchant;

    MerchantController(RegisterMerchantUseCase registerMerchant) {
        this.registerMerchant = registerMerchant;
    }

    @PostMapping
    ResponseEntity<IdResponse> create(@RequestBody CreateMerchantRequest request) {
        long id = registerMerchant.register(request.partner());
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    record CreateMerchantRequest(boolean partner) {
    }
}
