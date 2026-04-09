package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Page not found"),
        HttpStatus.NOT_FOUND);
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPaymentRequest(MethodArgumentNotValidException ex){
    LOG.debug(ex.getMessage());
  return new ResponseEntity<>(new ErrorResponse("Invalid Parameters used in the payments request"),
    HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BankRequestException.class)
  public ResponseEntity<PostPaymentResponse> handleInvalidPaymentRequest(BankRequestException ex){
    LOG.debug(ex.getMessage());
    return new ResponseEntity<>(ex.getErrorPostPaymentResponse(),
        HttpStatus.SERVICE_UNAVAILABLE);
  }

}