package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.annotation.ValidExpiryDate;
import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.enums.PaymentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

@ValidExpiryDate
public record PostPaymentRequest(
    @NotBlank
    @Pattern(regexp = "\\d{14,19}")
    String cardNumber,
    @NotNull
    @Max(12) @Min(1)
    Integer expiryMonth,
    @NotNull
    Integer expiryYear,
    @NotNull
    CurrencyCode currency,
    @NotNull
    @Min(0)
    Integer amount,
    @NotBlank
    @Pattern(regexp = "\\d{3,4}")
    String cvv) {

  public Integer getCardNumberLastFour(){
    return Integer.valueOf(this.cardNumber.substring(cardNumber.length() - 4));
  }

  public PostPaymentResponse toPostPaymentResponse(PaymentStatus status){
    UUID id = UUID.randomUUID();
    return new PostPaymentResponse(
      UUID.randomUUID(),
        status,
        getCardNumberLastFour(),
        expiryMonth(),
        expiryYear(),
        currency(),
        amount()
    );
  }

  @Override
  @NotNull
  public String toString() {
    String lastFourNumbers = cardNumber.substring(cardNumber.length() - 4);
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
