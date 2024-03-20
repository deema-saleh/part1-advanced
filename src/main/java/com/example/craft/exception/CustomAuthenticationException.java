package com.example.craft.exception;

public class CustomAuthenticationException extends RuntimeException {

  public CustomAuthenticationException(String message) {
    super(message);
  }
}
