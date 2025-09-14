package com.auth.authApp.exceptions;

public class EmailOrPasswordIncorrectException extends RuntimeException {
    public EmailOrPasswordIncorrectException(String message){super(message);}
}
