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

    private static final int GROCERIES_MCC = 5411; // 2%

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Should credit the calculated cashback to the customer's running rewards balance")
    class ShouldCreditCashbackToTheRunningBalance {

        @Test
        @DisplayName("The one where a customer with a 4.00 balance earns 2.00 and ends at 6.00")
        void earnedCashbackIsAddedToTheExistingBalance() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant();

            purchase(customerId, merchantId, "200.00"); // earns 4.00 -> balance 4.00
            purchase(customerId, merchantId, "100.00"); // earns 2.00 -> balance 6.00

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value("6.00"));
        }

        @Test
        @DisplayName("The one where cashback rounds to 0.00 -> balance is unchanged")
        void cashbackThatRoundsToZeroLeavesTheBalanceUnchanged() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant();

            purchase(customerId, merchantId, "200.00"); // earns 4.00 -> balance 4.00
            purchase(customerId, merchantId, "0.01"); // earns 0.00 -> balance unchanged

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value("4.00"));
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
            long merchantId = createMerchant();

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(purchaseBody(customerId, merchantId, "0.00"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where the purchase amount is negative -> rejected (400)")
        void aNegativeAmountIsRejected() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant();

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(purchaseBody(customerId, merchantId, "-10.00"))))
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
                            .content(json(purchaseBody(customerId, unknownMerchantId, "100.00"))))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("The one where the customer ID doesn't exist -> rejected (404), distinct from an invalid amount (400)")
        void anUnknownCustomerIsRejected() throws Exception {
            long merchantId = createMerchant();
            long unknownCustomerId = 999_999L;

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(purchaseBody(unknownCustomerId, merchantId, "100.00"))))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Should reverse cashback from the customer's balance when a purchase is refunded")
    class ShouldReverseCashbackWhenAPurchaseIsRefunded {

        @Test
        @DisplayName("The one where a customer earned 2.00 (balance 2.00), the purchase is fully refunded, and the balance returns to 0.00")
        void aFullRefundReversesAllCashback() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant();
            long purchaseId = purchaseReturningId(customerId, merchantId, "100.00"); // earns 2.00 -> balance 2.00

            mockMvc.perform(post("/refunds")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "purchaseId", purchaseId,
                                    "amount", "100.00"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashbackReversed").value("2.00"));

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value("0.00"));
        }

        @Test
        @DisplayName("The one where a 100.00 purchase at 2% is partially refunded 40.00 -> cashback reversed is 0.80")
        void aPartialRefundReversesCashbackProportionally() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant();
            long purchaseId = purchaseReturningId(customerId, merchantId, "100.00"); // earns 2.00 -> balance 2.00

            mockMvc.perform(post("/refunds")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "purchaseId", purchaseId,
                                    "amount", "40.00"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashbackReversed").value("0.80"));

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value("1.20"));
        }
    }

    @Nested
    @DisplayName("Must reject an invalid refund")
    class MustRejectAnInvalidRefund {

        @Test
        @DisplayName("The one where the refund references an unknown purchase -> rejected (404)")
        void aRefundForAnUnknownPurchaseIsRejected() throws Exception {
            long unknownPurchaseId = 999_999L;

            mockMvc.perform(post("/refunds")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "purchaseId", unknownPurchaseId,
                                    "amount", "100.00"))))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("The one where cumulative refunds would exceed the original purchase amount -> rejected (409)")
        void refundsExceedingTheOriginalPurchaseAmountAreRejected() throws Exception {
            long customerId = createCustomer();
            long merchantId = createMerchant();
            long purchaseId = purchaseReturningId(customerId, merchantId, "100.00");

            mockMvc.perform(post("/refunds")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "purchaseId", purchaseId,
                                    "amount", "60.00"))))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/refunds")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "purchaseId", purchaseId,
                                    "amount", "60.00"))))
                    .andExpect(status().isConflict());
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

    private long createMerchant() throws Exception {
        MvcResult result = mockMvc.perform(post("/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("partner", true))))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    private void purchase(long customerId, long merchantId, String amount) throws Exception {
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(purchaseBody(customerId, merchantId, amount))))
                .andExpect(status().isCreated());
    }

    private long purchaseReturningId(long customerId, long merchantId, String amount) throws Exception {
        MvcResult result = mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(purchaseBody(customerId, merchantId, amount))))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    private Map<String, ?> purchaseBody(long customerId, long merchantId, String amount) {
        return Map.of(
                "customerId", customerId,
                "merchantId", merchantId,
                "amount", amount,
                "mcc", GROCERIES_MCC,
                "type", "purchase",
                "status", "posted",
                "cardState", "active");
    }

    private long idOf(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private String json(Map<String, ?> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }
}
