package com.example.craft.controllers;

import com.example.craft.models.Project;
import com.example.craft.models.ProjectParticipant;
import com.example.craft.models.Task;
import com.example.craft.models.User;
import com.example.craft.payloads.request.UserReq;
import com.example.craft.payloads.response.ProjectResp;
import com.example.craft.payloads.response.TaskResp;
import com.example.craft.payloads.response.UserResp;
import com.example.craft.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:9090"}, maxAge = 3600)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/user")
public class UserController {

  private final UserDetailsServiceImpl userService;
  private final TaskService taskService;

  @Autowired
  public UserController(UserDetailsServiceImpl userService, TaskService taskService) {
    this.taskService = taskService;
    this.userService = userService;
  }

  @Operation(summary = "Get user tasks", description = "Retrieves a list of tasks associated with the specified username")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tasks"),
          @ApiResponse(responseCode = "404", description = "Username not found")
  })
  @GetMapping("/{username}/tasks")
  public ResponseEntity<List<TaskResp>> getUserTasks(@PathVariable String username) {
    List<Task> tasks = taskService.findTasksByUser(username);
    List<TaskResp> taskResps = tasks.stream()
            .map(TaskResp::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(taskResps);
  }

  @Operation(summary = "Get user projects", description = "Retrieves a list of projects associated with the specified username")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved list of projects"),
          @ApiResponse(responseCode = "404", description = "Username not found")
  })
  @GetMapping("/{username}/projects")
  public ResponseEntity<List<ProjectResp>> getUserProjects(@PathVariable String username) {
    List<Project> projects = userService.findByUsername(username)
            .getProjectParticipants()
            .stream()
            .map(ProjectParticipant::getProject)
            .toList();

    List<ProjectResp> projectResps = projects.stream()
            .map(ProjectResp::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(projectResps);
  }
  @Operation(summary = "Update user information", description = "Updates the information for the user specified by username. The request must match the username in the path.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User information updated successfully"),
          @ApiResponse(responseCode = "400", description = "Bad request, e.g., username mismatch"),
          @ApiResponse(responseCode = "404", description = "Username not found")
  })
  @PutMapping("/{username}")
  public ResponseEntity<UserResp> updateUser(@PathVariable String username, @RequestBody UserReq userUpdateRequest) {
    try {
      if (!username.equals(userUpdateRequest.getUsername()))
        return ResponseEntity.badRequest().build();

      User user = userService.findByUsername(username);
      User userUpdateRequestEntity = userUpdateRequest.toEntity();

      user.setUserSkills(userUpdateRequestEntity.getUserSkills());
      user.setFirstName(userUpdateRequestEntity.getFirstName());
      user.setLastName(userUpdateRequestEntity.getLastName());
      user.setRegion(userUpdateRequestEntity.getRegion());
      user.setProfileImg(userUpdateRequestEntity.getProfileImg());

      User updatedUser = userService.save(user);
      return ResponseEntity.ok(new UserResp(updatedUser));
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
