package com.example.craft.payloads.request;



public abstract class AbstractRequest <T>{
  public abstract T toEntity();
}
