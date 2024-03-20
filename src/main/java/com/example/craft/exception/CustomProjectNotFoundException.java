package com.example.craft.exception;

public class CustomProjectNotFoundException extends RuntimeException {
  public CustomProjectNotFoundException(String message) {
    super(message);
  }
}
