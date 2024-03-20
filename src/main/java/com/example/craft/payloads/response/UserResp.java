package com.example.craft.payloads.response;

import com.example.craft.models.User;
import com.example.craft.models.UserSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UserResp extends AbstractResponse<User, UserResp> {
  private String username;
  private String firstName;
  private String lastName;
  private String region;
  private String profileImg;
  private List<UserSkill> userSkills;

  public UserResp(User user) {
    this.toResp(user);
  }

  @Override
  public UserResp toResp(User user) {
    setId(user.getId());
    this.username = user.getUsername();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.region = user.getRegion();
    this.profileImg = user.getProfileImg();
    this.userSkills = user.getUserSkills();
    return this;
  }
}
