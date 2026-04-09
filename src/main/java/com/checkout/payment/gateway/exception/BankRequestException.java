package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.PostPaymentResponse;

public class BankRequestException extends RuntimeException {

  private final PostPaymentResponse errorPostPaymentResponse;

  public BankRequestException(String message, PostPaymentResponse errorPostPaymentResponse) {
    super(message);
    this.errorPostPaymentResponse = errorPostPaymentResponse;
  }

  public PostPaymentResponse getErrorPostPaymentResponse() {
    return errorPostPaymentResponse;
  }
}
