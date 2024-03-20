package com.example.craft.payloads.response;


import com.example.craft.models.ProjectParticipant;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectParticipantResp extends AbstractResponse<ProjectParticipant, ProjectParticipantResp> {
  private String roleId;
  private String status;
  private UserResp user;

  public ProjectParticipantResp(ProjectParticipant projectParticipant) {
    this.toResp(projectParticipant);
  }

  @Override
  public ProjectParticipantResp toResp(ProjectParticipant projectParticipant) {
    setId(projectParticipant.getId());
    this.roleId = projectParticipant.getRoleId();
    this.status = projectParticipant.getStatus();
    this.user = new UserResp(projectParticipant.getUser());

    return this;
  }
}