package com.example.craft.models;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Accessors(chain = true)
public class Project extends BaseDTO {
  private String projectName;
  @Column(length = 5000)
  private String description;
  private String projectCategory;
  private String projectMaterials;
  private Integer estimatedDurationInDays;
  private String imageUrlOrThumbnail;
  private String status; // Enum can be used instead of String

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Task> tasks;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProjectParticipant> projectParticipants;

}