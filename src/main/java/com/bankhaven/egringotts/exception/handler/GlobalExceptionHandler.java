package com.bankhaven.egringotts.exception.handler;

import com.bankhaven.egringotts.exception.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    protected Map<String, String> handleAccountNotFoundException(AccountNotFoundException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    protected Map<String, String> handleUserNotFoundException(UserNotFoundException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientBalanceException.class)
    protected Map<String, String> handleInsufficientBalanceException(InsufficientBalanceException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyInUseException.class)
    protected Map<String, String> handleEmailAlreadyInUseException(EmailAlreadyInUseException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DistrictNotFoundException.class)
    protected Map<String, String> handleDistrictNotFoundException(DistrictNotFoundException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlaceNotFoundException.class)
    protected Map<String, String> handlePlaceNotFoundException(PlaceNotFoundException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    protected Map<String, String> handleBadCredentialsException(BadCredentialsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }

    @ExceptionHandler(AccountTierNotExistException.class)
    public ResponseEntity<String> handleAccountTierNotExistException(AccountTierNotExistException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientInitialFundException.class)
    public ResponseEntity<String> handleInsufficientInitialFundException(InsufficientInitialFundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) mostSpecificCause;
            String targetType = ife.getTargetType().getSimpleName();
            String errorMessage = "Invalid account type " + targetType + ": " + ife.getValue();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request message");
    }

    static class ErrorDetails {
        private String message;

        public ErrorDetails(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
