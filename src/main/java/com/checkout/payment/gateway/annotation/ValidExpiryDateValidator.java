package com.checkout.payment.gateway.annotation;

import com.checkout.payment.gateway.annotation.ValidExpiryDate;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;
import java.time.ZoneId;

public class ValidExpiryDateValidator implements ConstraintValidator<ValidExpiryDate, PostPaymentRequest> {

  @Override
  public boolean isValid(PostPaymentRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    if (request.expiryMonth() == null || request.expiryYear() == null) {
      return true;
    }

    if (request.expiryMonth() < 1 || request.expiryMonth() > 12) {
      return false;
    }

    YearMonth expiry = YearMonth.of(request.expiryYear(), request.expiryMonth());
    YearMonth current = YearMonth.now(ZoneId.systemDefault());

    return expiry.isAfter(current);
  }
}