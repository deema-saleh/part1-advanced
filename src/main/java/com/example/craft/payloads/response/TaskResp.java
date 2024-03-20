package com.example.craft.payloads.response;

import com.example.craft.models.Task;
import com.example.craft.models.User;
import lombok.*;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResp extends AbstractResponse<Task, TaskResp> {
  private String title;
  private String description;
  private Date dueDate;
  private String status;
  private Date lastUpdatedDate;
  private UserResp assignedUser;

  public TaskResp(Task task) {
    toResp(task);
  }
  @Override
  public TaskResp toResp(Task task) {
    setId(task.getId());
    this.title = task.getTitle();
    this.description = task.getDescription();
    this.dueDate = task.getDueDate();
    this.status = task.getStatus();
    this.lastUpdatedDate = task.getLastUpdatedDate();
    this.assignedUser = new UserResp(task.getAssignedUser());

    return this;
  }
}