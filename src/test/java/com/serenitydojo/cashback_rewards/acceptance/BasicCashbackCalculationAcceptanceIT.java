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

    private long idOf(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private String json(Map<String, ?> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }
}
