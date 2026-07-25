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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MerchantCategoriesAndEligibilityAcceptanceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Should apply the cashback rate for the transaction's MCC at a partner merchant")
    class ShouldApplyTheMccRateAtAPartnerMerchant {

        @Test
        @DisplayName("The one where a 100.00 posted purchase with MCC 5411 (groceries) on an active card at a partner merchant earns 2.00")
        void aGroceryMccPurchaseEarnsTwoPercent() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "active"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("2.00"));
        }

        @Test
        @DisplayName("The one where a 100.00 posted purchase with MCC 5541 (fuel) at a partner merchant earns 1.00")
        void aFuelMccPurchaseEarnsOnePercent() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5541,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "active"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("1.00"));
        }

        @Test
        @DisplayName("The one where the MCC is unrecognised -> the 0.5% default applies -> 100.00 earns 0.50")
        void anUnrecognisedMccEarnsTheDefaultRate() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5999,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "active"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("0.50"));
        }

        @Test
        @DisplayName("The one where the merchant is a non-partner -> the rate is gated to 0%, so a 100.00 grocery-MCC purchase earns 0.00")
        void aNonPartnerMerchantEarnsNothing() throws Exception {
            long customerId = createCustomer();
            long nonPartnerMerchant = createMerchant(false);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", nonPartnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "active"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("0.00"));
        }
    }

    @Nested
    @DisplayName("Must only award cashback on an eligible transaction - a posted purchase on an active card")
    class MustOnlyAwardCashbackOnAnEligibleTransaction {

        @Test
        @DisplayName("The one where a posted purchase on an active card is accepted and credits cashback")
        void aPostedPurchaseOnAnActiveCardIsAccepted() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "active"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cashback").value("2.00"));
        }

        @Test
        @DisplayName("The one where every attribute qualifies except the card is frozen -> rejected (409)")
        void aFrozenCardIsRejected() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "frozen"))))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("The one where the type is not a recognised transaction type -> rejected (400), distinct from an eligibility conflict (409)")
        void anUnrecognisedTypeIsRejected() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "chargeback",
                                    "status", "posted",
                                    "cardState", "active"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where the status is not a recognised transaction status -> rejected (400)")
        void anUnrecognisedStatusIsRejected() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "settled",
                                    "cardState", "active"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where the card state is not a recognised card state -> rejected (400)")
        void anUnrecognisedCardStateIsRejected() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "posted",
                                    "cardState", "expired"))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where the card state is missing altogether -> rejected (400)")
        void aMissingCardStateIsRejected() throws Exception {
            long customerId = createCustomer();
            long partnerMerchant = createMerchant(true);

            mockMvc.perform(post("/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "customerId", customerId,
                                    "merchantId", partnerMerchant,
                                    "amount", "100.00",
                                    "mcc", 5411,
                                    "type", "purchase",
                                    "status", "posted"))))
                    .andExpect(status().isBadRequest());
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

    private long createMerchant(boolean partner) throws Exception {
        MvcResult result = mockMvc.perform(post("/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("partner", partner))))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    private long idOf(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private String json(Map<String, ?> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }
}
