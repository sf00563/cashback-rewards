package com.serenitydojo.cashback_rewards.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BasicCashbackCalculationAcceptanceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Should apply the cashback rate configured for the purchase's merchant")
    class ShouldApplyTheMerchantsConfiguredRate {

        @Test
        @DisplayName("The one where the same 100.00 purchase yields 5.00 at a 5% merchant but 2.00 at a 2% merchant")
        void appliesEachMerchantsOwnRateToTheSamePurchaseAmount() throws Exception {
            long customerId = createCustomer();
            long fivePercentMerchant = createMerchant("5");
            long twoPercentMerchant = createMerchant("2");

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", fivePercentMerchant,
                                    "amount", "100.00"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("5.00"));

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", twoPercentMerchant,
                                    "amount", "100.00"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("2.00"));
        }

        @Test
        @DisplayName("The one where the merchant is configured at 0% -> cashback is 0.00 (valid excluded partner)")
        void aZeroPercentMerchantYieldsZeroCashback() throws Exception {
            long customerId = createCustomer();
            long zeroPercentMerchant = createMerchant("0");

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", zeroPercentMerchant,
                                    "amount", "100.00"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("0.00"));
        }
    }

    @Nested
    @DisplayName("Should credit the calculated cashback to the customer's running rewards balance")
    class ShouldCreditCashbackToTheRunningBalance {

        @Test
        @DisplayName("The one where a customer with a 10.00 balance earns 5.00 and ends at 15.00")
        void earnedCashbackIsAddedToTheExistingBalance() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant("5");

            purchase(customerId, merchantId, "200.00"); // earns 10.00 -> balance 10.00
            purchase(customerId, merchantId, "100.00"); // earns 5.00 -> balance 15.00

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value("15.00"));
        }

        @Test
        @DisplayName("The one where cashback rounds to 0.00 -> balance is unchanged")
        void cashbackThatRoundsToZeroLeavesTheBalanceUnchanged() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant("5");

            purchase(customerId, merchantId, "200.00"); // earns 10.00 -> balance 10.00
            purchase(customerId, merchantId, "0.01"); // earns 0.00 -> balance unchanged

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value("10.00"));
        }

        @Test
        @DisplayName("The one where querying an unknown customer ID is rejected (404)")
        void queryingAnUnknownCustomerIsRejected() throws Exception {
            long unknownCustomerId = 999_999L;

            mockMvc.perform(get("/customers/{id}", unknownCustomerId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Must reject a purchase with a non-positive amount")
    class MustRejectANonPositiveAmount {

        @Test
        @DisplayName("The one where the purchase amount is 0.00 -> rejected (400)")
        void aZeroAmountIsRejected() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant("5");

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", merchantId,
                                    "amount", "0.00"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where the purchase amount is negative -> rejected (400)")
        void aNegativeAmountIsRejected() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant("5");

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", merchantId,
                                    "amount", "-10.00"))))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Must reject a purchase referencing an unknown merchant or customer")
    class MustRejectAnUnknownMerchantOrCustomer {

        @Test
        @DisplayName("The one where the merchant ID doesn't exist -> rejected (404)")
        void anUnknownMerchantIsRejected() throws Exception {
            long customerId = createCustomer();
            long unknownMerchantId = 999_999L;

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", unknownMerchantId,
                                    "amount", "100.00"))))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("The one where the customer ID doesn't exist -> rejected (404), distinct from an invalid amount (400)")
        void anUnknownCustomerIsRejected() throws Exception {
            long merchantId = createMerchant("5");
            long unknownCustomerId = 999_999L;

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", unknownCustomerId,
                                    "merchantId", merchantId,
                                    "amount", "100.00"))))
                    .andExpect(status().isNotFound());
        }
    }

    private long createCustomer() throws Exception {
        MvcResult result = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    private long createMerchant(String cashbackRate) throws Exception {
        MvcResult result = mockMvc.perform(post("/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("cashbackRate", cashbackRate))))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    private void purchase(long customerId, long merchantId, String amount) throws Exception {
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "customerId", customerId,
                                "merchantId", merchantId,
                                "amount", amount))))
                .andExpect(status().isCreated());
    }

    private long idOf(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private String json(Map<String, ?> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }
}
