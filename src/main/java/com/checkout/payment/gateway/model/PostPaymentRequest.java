package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PostPaymentRequest(
    @NotBlank
    @Pattern(regexp = "\\d{14,19}")
    String cardNumber,
    @NotNull
    Integer expiryMonth,
    @NotNull
    Integer expiryYear,
    @NotNull
    CurrencyCode currency,
    @NotNull
    Integer amount,
    @NotBlank
    @Pattern(regexp = "\\d{3,4}")
    String cvv) {

  @Override
  @NotNull
  public String toString() {
    String lastFourNumbers = cardNumber.substring(cardNumber.length() - 4, cardNumber().length());
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + lastFourNumbers +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" +  cvv +
        '}';
  }
}
