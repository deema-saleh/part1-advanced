package com.example.craft.payloads.request;

import com.example.craft.models.Project;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
public class ProjectRequest extends AbstractRequest<Project> {
  @NotBlank
  private String projectName;
  @Nullable
  private String description;
  @NotBlank
  private String projectCategory;
  @NotBlank
  private String projectMaterials;
  @Nullable
  private Integer estimatedDurationInDays;
  @Nullable
  private String imageUrlOrThumbnail;

  @Override
  public Project toEntity() {
    return Project.builder()
        .projectName(projectName)
        .description(description)
        .projectCategory(projectCategory)
        .projectMaterials(projectMaterials)
        .estimatedDurationInDays(estimatedDurationInDays)
        .imageUrlOrThumbnail(imageUrlOrThumbnail)
        .build();
  }
}
