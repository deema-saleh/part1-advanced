package com.example.craft.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class UserSkill extends BaseDTO {

  @ManyToOne
  @JoinColumn(name = "userId")
  @JsonIgnore
  private User user;

  @ManyToOne
  @JoinColumn(name = "skillId")
  private PredefinedSkill predefinedSkill;
}