package com.checkout.payment.gateway.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidExpiryDateValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidExpiryDate {
  String message() default "Expiry date must be in the future";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}