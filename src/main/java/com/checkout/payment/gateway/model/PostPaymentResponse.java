package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PostPaymentResponse(UUID id,
                                  PaymentStatus status,
                                  Integer cardNumberLastFour,
                                  Integer expiryMonth,
                                  Integer expiryYear,
                                  CurrencyCode currency,
                                  Integer amount) {

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
