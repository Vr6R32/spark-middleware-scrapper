package com.spark.data.infrastructure.exception;

import com.spark.data.domain.currency_pair.CurrencyPairException;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.response.CurrencyPairResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CurrencyPairExceptionHandler {

    @ExceptionHandler(CurrencyPairException.class)
    public ResponseEntity<CurrencyPairResponse> handleCurrencyPairException(CurrencyPairException ex) {
        CurrencyPairResponse response = new CurrencyPairResponse(ex.getMessage(), CurrencyPairDTO.builder().symbol(ex.getSymbol()).build());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}