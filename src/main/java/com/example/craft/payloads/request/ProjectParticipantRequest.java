package com.example.craft.payloads.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectParticipantRequest {
  private String username;
  private String roleId;
}
