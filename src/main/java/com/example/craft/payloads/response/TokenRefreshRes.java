package com.example.craft.payloads.response;

import lombok.Data;

@Data
public class TokenRefreshRes {
  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";

  public TokenRefreshRes(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }


}
