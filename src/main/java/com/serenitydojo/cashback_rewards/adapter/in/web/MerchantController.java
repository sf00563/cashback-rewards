package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.RegisterMerchantService;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/merchants")
class MerchantController {

    private final RegisterMerchantService registerMerchantService;

    MerchantController(RegisterMerchantService registerMerchantService) {
        this.registerMerchantService = registerMerchantService;
    }

    @PostMapping
    ResponseEntity<IdResponse> create(@RequestBody CreateMerchantRequest request) {
        long id = registerMerchantService.register(new CashbackRate(request.cashbackRate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    record CreateMerchantRequest(BigDecimal cashbackRate) {
    }
}
