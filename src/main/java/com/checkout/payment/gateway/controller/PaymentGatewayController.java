package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("api")
@Tag(name = "Payments", description = "Payment processing and retrieval endpoints")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @GetMapping("/payments/{id}")
  @Operation(summary = "Retrieve payment by id", description = "Returns a previously processed payment.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Payment found",
        content = @Content(schema = @Schema(implementation = PostPaymentResponse.class))),
      @ApiResponse(responseCode = "404", description = "Payment not found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(
      @Parameter(description = "Payment identifier", required = true) @PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  @PostMapping("/payments")
  @Operation(summary = "Process payment", description = "Validates and processes a card payment request.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Payment processed",
        content = @Content(schema = @Schema(implementation = PostPaymentResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid payment request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "Acquiring bank unavailable; response body status is Declined",
        content = @Content(schema = @Schema(implementation = PostPaymentResponse.class), examples =
        @ExampleObject(name = "Declined due to bank unavailability", value = "{" +
          "\"id\":\"d290f1ee-6c54-4b01-90e6-d701748f0851\"," +
          "\"status\":\"Declined\"," +
          "\"cardNumberLastFour\":8870," +
          "\"expiryMonth\":12," +
          "\"expiryYear\":2030," +
          "\"currency\":\"USD\"," +
          "\"amount\":1050" +
          "}")))
  })
  public ResponseEntity<PostPaymentResponse> processPayment(
      @Parameter(description = "Payment request payload", required = true)
      @Valid @RequestBody PostPaymentRequest paymentRequest) {
    return new ResponseEntity<>(paymentGatewayService.processPayment(paymentRequest), HttpStatus.OK);
  }
}
