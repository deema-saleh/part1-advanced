package com.example.craft.payloads.response;

import com.example.craft.models.Project;
import com.example.craft.models.ProjectParticipant;
import com.example.craft.models.Task;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectResp extends AbstractResponse<Project, ProjectResp> {
  private String projectName;
  private String description;
  private String projectCategory;
  private String projectMaterials;
  private Integer estimatedDurationInDays;
  private String imageUrlOrThumbnail;
  private String status;
  private List<TaskResp> tasks;
  private List<ProjectParticipantResp> projectParticipants;

  public ProjectResp(Project project) {
    toResp(project);
  }

  @Override
  public ProjectResp toResp(Project project) {
    setId(project.getId());
    this.projectName = project.getProjectName();
    this.description = project.getDescription();
    this.projectCategory = project.getProjectCategory();
    this.projectMaterials = project.getProjectMaterials();
    this.estimatedDurationInDays = project.getEstimatedDurationInDays();
    this.imageUrlOrThumbnail = project.getImageUrlOrThumbnail();
    this.status = project.getStatus();
    List<Task> taskList = project.getTasks();
    this.tasks = taskList == null ? null : taskList.stream().map(TaskResp::new).toList();
    List<ProjectParticipant> projectParticipantList = project.getProjectParticipants();
    this.projectParticipants = projectParticipantList == null ? null : projectParticipantList.stream().map(ProjectParticipantResp::new).toList();
    return this;
  }
}