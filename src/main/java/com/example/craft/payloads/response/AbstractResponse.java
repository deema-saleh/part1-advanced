package com.example.craft.payloads.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractResponse<T, R> {
  @JsonInclude
  @Setter
  @Getter
  private Long id;

  public abstract R toResp(T t);
}
