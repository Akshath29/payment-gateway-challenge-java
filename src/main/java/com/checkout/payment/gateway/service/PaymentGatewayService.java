package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatusCode;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final RestTemplate restTemplate;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, RestTemplate restTemplate) {
    this.paymentsRepository = paymentsRepository;
    this.restTemplate = restTemplate;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    LOG.debug("Requesting payment");

    PostPaymentResponse paymentResponse  = sendPaymentToBank(paymentRequest);
    paymentsRepository.add(paymentResponse);
    return paymentResponse;
  }


  // TODO: ADD PROPER LOGS
  private PostPaymentResponse sendPaymentToBank(PostPaymentRequest paymentRequest) {
    LOG.debug(("Sending request to bank"));
    LocalDate date = LocalDate.now();
    String expiryDate = String.format("%02d/%d", paymentRequest.expiryMonth(), paymentRequest.expiryYear());
    if(!LocalDate.of(paymentRequest.expiryYear(), paymentRequest.expiryMonth(), 1).isAfter(date)){
      throw new EventProcessingException("Card expiry date is before the current date");
    }

    BankPaymentRequest bankRequest = new BankPaymentRequest(
        paymentRequest.cardNumber(),
        expiryDate,
        paymentRequest.currency().getName(),
        paymentRequest.amount(),
        paymentRequest.cvv()
    );

    ResponseEntity<BankPaymentResponse> bankResponse =
        restTemplate.postForEntity(
            "http://localhost:8080/payments",
            bankRequest,
            BankPaymentResponse.class
        );


    return handleBankResponse(bankResponse, paymentRequest);
  }

  private PostPaymentResponse handleBankResponse(ResponseEntity<BankPaymentResponse> bankResponse, PostPaymentRequest paymentRequest) {
    UUID id = UUID.randomUUID();
    // TODO: MAKE THIS INTO A EXCEPTION
    if(bankResponse.getBody() == null){
      throw new EventProcessingException("Bank responded with null");
    }
    if(bankResponse.getStatusCode().is2xxSuccessful() && !bankResponse.getBody().authorized()) {
        return new PostPaymentResponse(
            id,
            PaymentStatus.DECLINED,
            paymentRequest.getCardNumberLastFour(),
            paymentRequest.expiryMonth(),
            paymentRequest.expiryYear(),
            paymentRequest.currency(),
            paymentRequest.amount());
      }

      if(bankResponse.getStatusCode().is4xxClientError()){
        return new PostPaymentResponse(
            id,
            PaymentStatus.REJECTED,
            paymentRequest.getCardNumberLastFour(),
            paymentRequest.expiryMonth(),
            paymentRequest.expiryYear(),
            paymentRequest.currency(),
            paymentRequest.amount());
      }

      if(bankResponse.getStatusCode().is5xxServerError()){
        return new PostPaymentResponse(
            id,
            PaymentStatus.DECLINED,
            paymentRequest.getCardNumberLastFour(),
            paymentRequest.expiryMonth(),
            paymentRequest.expiryYear(),
            paymentRequest.currency(),
            paymentRequest.amount());
      }

    return new PostPaymentResponse(
        id,
        PaymentStatus.AUTHORIZED,
        paymentRequest.getCardNumberLastFour(),
        paymentRequest.expiryMonth(),
        paymentRequest.expiryYear(),
        paymentRequest.currency(),
        paymentRequest.amount());
  }
}

