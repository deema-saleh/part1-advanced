package com.example.craft.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class PredefinedSkill extends BaseDTO {
  private String skillName;
  @Column(length = 5000)
  private String skillDescription;

  @OneToMany(mappedBy = "predefinedSkill")
  @JsonIgnore
  private List<UserSkill> userSkills;

}