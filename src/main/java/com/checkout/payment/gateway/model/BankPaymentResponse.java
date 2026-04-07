package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record BankPaymentResponse(Boolean authorized,
                                  @NotBlank
                                  @JsonProperty("authorization_code")
                                  String authorizationCode)
{}
