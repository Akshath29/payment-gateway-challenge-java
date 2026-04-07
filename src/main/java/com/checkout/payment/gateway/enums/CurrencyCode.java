package com.checkout.payment.gateway.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyCode {
  GBP("GBP"),
  USD("USD"),
  EUR("EUR");

  private final String name;

  CurrencyCode(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }
}
