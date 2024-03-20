package com.example.craft.payloads.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginReq {
	@NotBlank
  private String username;

	@NotBlank
	private String password;

}
