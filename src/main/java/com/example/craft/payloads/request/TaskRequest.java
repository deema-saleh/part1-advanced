package com.example.craft.payloads.request;

import com.example.craft.config.UserServiceInjector;
import com.example.craft.models.Task;
import com.example.craft.models.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
public class TaskRequest extends AbstractRequest<Task> {
  @NotBlank
  private String title;
  private String description;
  private Date dueDate;
  private String assignedUsername;
  private String status;

  @Override
  public Task toEntity() {
    User user = UserServiceInjector.getUserDetailsService().findByUsername(assignedUsername);
    return Task.builder()
            .title(title)
            .description(description)
            .dueDate(dueDate)
            .assignedUser(user)
            .status(status)
            .build();
  }
}