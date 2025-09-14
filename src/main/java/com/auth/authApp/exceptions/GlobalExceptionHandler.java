package com.auth.authApp.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handelValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handelEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        log.warn("Email already exists {},",ex.getMessage());
        Map<String,String>error = new HashMap<>();
        error.put("massage","Email already exists");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EmailOrPasswordIncorrectException.class)
    public ResponseEntity<Map<String, Object>> handleEmailOrPasswordIncorrectException(EmailOrPasswordIncorrectException ex){
        log.warn("Email or Password is Incorrect {}",ex.getMessage());
        Map<String,Object>error = new HashMap<>();
        error.put("error",true);
        error.put("message","Email or Password is Incorrect");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDisableException(AccountDisabledException ex){
        log.warn("Account is disabled {}",ex.getMessage());
        Map<String,Object>error = new HashMap<>();
        error.put("error",true);
        error.put("message","Account is disabled");
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailedException(AuthenticationFailedException ex){
        log.warn("Authentication failed {}",ex.getMessage());
        Map<String,Object>error = new HashMap<>();
        error.put("error",true);
        error.put("message","Authentication Failed");
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(UserNotFloundException.class)
    public ResponseEntity<Map<String,String>> handelUserNotFoundException(UserNotFloundException ex){
        log.warn("User not found {}",ex.getMessage());
        Map<String,String>error = new HashMap<>();
        error.put("massage","User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
