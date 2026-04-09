package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankRequestException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    PostPaymentResponse paymentResponse = sendPaymentToBank(paymentRequest);
    paymentsRepository.add(paymentResponse);
    return paymentResponse;
  }


  private PostPaymentResponse sendPaymentToBank(PostPaymentRequest paymentRequest) {
    String expiryDate = String.format("%02d/%d", paymentRequest.expiryMonth(),
        paymentRequest.expiryYear());
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
    LOG.debug(("Request sent to bank"));

    return handleBankResponse(bankResponse, paymentRequest);
  }

  private PostPaymentResponse handleBankResponse(ResponseEntity<BankPaymentResponse> bankResponse,
      PostPaymentRequest paymentRequest) {
    if (bankResponse.getBody() == null) {
      LOG.debug("Bank returning null");
      return paymentRequest.toPostPaymentResponse(PaymentStatus.DECLINED);
    }
    if (bankResponse.getStatusCode().is2xxSuccessful() && bankResponse.getBody().authorized()) {
      return paymentRequest.toPostPaymentResponse(PaymentStatus.AUTHORIZED);
    }
    if (bankResponse.getStatusCode().is5xxServerError()){
      LOG.debug("Error with request to bank {}", bankResponse.getBody());
      throw new BankRequestException("Bank unavailable", paymentRequest.toPostPaymentResponse(PaymentStatus.DECLINED));
    }


    return paymentRequest.toPostPaymentResponse(PaymentStatus.DECLINED);
  }
}

