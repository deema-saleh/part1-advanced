package com.example.craft.payloads.request;

import com.example.craft.models.User;
import com.example.craft.models.UserSkill;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
public class UserReq extends AbstractRequest<User> {
  @NotBlank
  private String username;
  private String firstName;
  private String lastName;
  private String region;
  private String profileImg;
  private List<UserSkill> userSkills;

  @Override
  public User toEntity() {
    return User.builder()
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .region(region)
            .profileImg(profileImg)
            .userSkills(userSkills)
            .build();
  }
}
