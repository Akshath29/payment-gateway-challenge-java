package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PostPaymentResponse(UUID id,
                                  PaymentStatus status,
                                  int cardNumberLastFour,
                                  int expiryMonth,
                                  int expiryYear,
                                  CurrencyCode currency,
                                  int amount) {

  @Override
  @NotNull
  public String toString() {
    return "PaymentResponse{" +
        "id=" + id +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}
