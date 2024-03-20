package com.example.craft.payloads.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshReq {
    @NotBlank
    private String refreshToken;

}
