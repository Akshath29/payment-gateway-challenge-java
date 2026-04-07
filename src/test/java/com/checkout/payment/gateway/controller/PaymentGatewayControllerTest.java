package com.checkout.payment.gateway.controller;


import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;
  @MockBean
  private RestTemplate restTemplate;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse(UUID.randomUUID(),
        PaymentStatus.AUTHORIZED,
        4321,
        12,
        2024,
        CurrencyCode.USD,
        10 );

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.status().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.cardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.expiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.expiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.currency()))
        .andExpect(jsonPath("$.amount").value(payment.amount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenPostPaymentRequestIsValidThenPaymentIsReturned() throws Exception {
    when(restTemplate.postForEntity(eq("http://localhost:8080/payments"), any(), eq(BankPaymentResponse.class)))
        .thenReturn(new ResponseEntity<>(new BankPaymentResponse(true, UUID.randomUUID().toString()),
            HttpStatus.OK));

    String requestBody = """
        {
          "cardNumber": "2222405343248877",
          "expiryMonth": 12,
          "expiryYear": 2030,
          "currency": "USD",
          "amount": 1050,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment/request")
            .contentType("application/json")
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").isNumber())
        .andExpect(jsonPath("$.expiryMonth").value(12))
        .andExpect(jsonPath("$.expiryYear").value(2030))
        .andExpect(jsonPath("$.currency").value(CurrencyCode.USD.getName()))
        .andExpect(jsonPath("$.amount").value(1050));
  }

  @Test
  void whenPostPaymentRequestIsInvalidThen400IsReturned() throws Exception {
    String invalidRequestBody = """
        {
          "cardNumber": "abc",
          "expiryMonth": 12,
          "expiryYear": 2030,
          "currency": "USD",
          "amount": 1050,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment/request")
            .contentType("application/json")
            .content(invalidRequestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", startsWith("Invalid Request Body")));
  }
}
