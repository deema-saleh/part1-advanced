package com.example.craft.controllers;

import com.example.craft.exception.CustomAuthenticationException;
import com.example.craft.exception.CustomAuthorizationException;
import com.example.craft.exception.CustomProjectNotFoundException;
import com.example.craft.models.Project;
import com.example.craft.models.ProjectParticipant;
import com.example.craft.models.Task;
import com.example.craft.models.User;
import com.example.craft.payloads.request.ProjectParticipantRequest;
import com.example.craft.payloads.request.ProjectRequest;
import com.example.craft.payloads.request.TaskRequest;
import com.example.craft.payloads.response.MessageRes;
import com.example.craft.payloads.response.ProjectResp;
import com.example.craft.services.BaseService;
import com.example.craft.services.ProjectService;
import com.example.craft.services.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController extends BaseController<Project, ProjectRequest, ProjectResp, Long> {

  private final ProjectService projectService;
  private final UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired
  public ProjectController(ProjectService projectService, UserDetailsServiceImpl userDetailsServiceImpl) {
    this.userDetailsServiceImpl = userDetailsServiceImpl;
    this.projectService = projectService;
  }

  @Override
  protected BaseService<Project, Long> getService() {
    return projectService;
  }

  @Override
  public ResponseEntity<ProjectResp> create(ProjectRequest entity) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Check if the user is authenticated
    if (authentication != null && authentication.isAuthenticated()) {
      // Get the principal which in case of JWT should be UserDetails
      Object principal = authentication.getPrincipal();

      if (principal instanceof UserDetails) {
        Project project = entity.toEntity();
        project
                .setStatus("Pending");

        Project save = getService().save(project);

        UserDetails userDetails = (UserDetails) principal;
        User user = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
        ProjectParticipant projectParticipant = new ProjectParticipant()
                .setUser(user)
                .setProject(save)
                .setStatus("Joined")
                .setRoleId("Admin");
        ArrayList<ProjectParticipant> arrayList = new ArrayList<>();
        arrayList.add(projectParticipant);
        save.setProjectParticipants(arrayList);

        save = getService().save(save);

        ProjectResp projectResp = new ProjectResp(save);

        return ResponseEntity.ok(projectResp);
      }
    }
    return ResponseEntity.badRequest().build();
  }
  @Operation( summary = "Add user to project", description = "Allows an admin to add a user to a project")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",description = "User added successfully", useReturnTypeSchema = true),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PostMapping("/user/{projectId}")
  public ResponseEntity<ProjectResp> addUser(@PathVariable Long projectId, ProjectParticipantRequest projectParticipantRequest) {
    Optional<Project> optionalProject = projectService.findById(projectId);
    if (optionalProject.isEmpty())
      return ResponseEntity.badRequest().build();

    Project project = optionalProject.get();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Check if the user is authenticated
    if (authentication != null && authentication.isAuthenticated()) {
      // Get the principal which in case of JWT should be UserDetails
      Object principal = authentication.getPrincipal();

      if (principal instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) principal;
        User user = userDetailsServiceImpl.findByUsername(userDetails.getUsername());

        User loaded = userDetailsServiceImpl.findByUsername(projectParticipantRequest.getUsername());
        ProjectParticipant projectParticipant = new ProjectParticipant()
                .setUser(loaded)
                .setProject(project)
                .setRoleId(projectParticipantRequest.getRoleId());

        if (project.getProjectParticipants().stream().noneMatch(p -> p.getUser().getId().equals(user.getId()) && p.getRoleId().equals("Admin"))) {
          if (!userDetails.getUsername().equals(projectParticipantRequest.getUsername()))
            return ResponseEntity.badRequest().build();

          projectParticipant.setStatus("Pending");
        } else {
          projectParticipant.setStatus("Joined");
        }
        project.getProjectParticipants().add(projectParticipant);
        ProjectResp projectResp = new ProjectResp(getService().save(project));
        return ResponseEntity.ok(projectResp);
      }
    }
    return ResponseEntity.badRequest().build();
  }
  @Operation(summary = "Delete user from project", description = "Allows an admin to remove a user from a project")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User removed successfully", useReturnTypeSchema = true),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "User or project not found")
  })
  @DeleteMapping("/user/{projectId}/{username}")
  public ResponseEntity<?> deleteUser(@PathVariable Long projectId, @PathVariable String username) {

    try {

      UserDetails userDetails = getAuthenticatedUserDetails();
      User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
      Project project = getProjectById(projectId);
      verifyUserIsAdmin(logedInUser, project);


      Optional<ProjectParticipant> participantToRemoveOpt = project.getProjectParticipants().stream()
              .filter(p -> p.getUser().getUsername().equals(username))
              .findFirst();

      if (participantToRemoveOpt.isEmpty()) {
        return ResponseEntity.badRequest().body(new MessageRes("User to remove not found in project"));
      }

      ProjectParticipant participantToRemove = participantToRemoveOpt.get();
      project.getProjectParticipants().remove(participantToRemove);
      projectService.save(project);

      return ResponseEntity.ok().body(new MessageRes("User removed from project successfully"));
    } catch (CustomAuthenticationException | CustomAuthorizationException | CustomProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageRes(e.getMessage()));
    }
  }
  @Operation(summary = "Accept pending user into project", description = "Allows an admin to accept a pending user into a project")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User status updated to 'Joined' successfully", useReturnTypeSchema = true),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PostMapping("/acceptUser/{projectId}/{username}")
  public ResponseEntity<?> acceptUser(@PathVariable Long projectId, @PathVariable String username) {
    try {
      UserDetails userDetails = getAuthenticatedUserDetails();
      User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
      Project project = getProjectById(projectId);
      verifyUserIsAdmin(logedInUser, project);

      Optional<ProjectParticipant> pendingUserOpt = project.getProjectParticipants().stream()
              .filter(p -> p.getUser().getUsername().equals(username) && p.getStatus().equals("Pending"))
              .findFirst();

      if (pendingUserOpt.isEmpty()) {
        return ResponseEntity.badRequest().body(new MessageRes("Pending user not found in project"));
      }


      ProjectParticipant pendingUser = pendingUserOpt.get();
      pendingUser.setStatus("Joined");
      projectService.save(project);

      return ResponseEntity.ok().body(new MessageRes("User status updated to 'Joined' successfully"));
    } catch (CustomAuthenticationException | CustomAuthorizationException | CustomProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageRes(e.getMessage()));
    }
  }
  @Operation(summary = "Accept pending user into project", description = "Allows an admin to accept a pending user into a project")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User status updated to 'Joined' successfully", useReturnTypeSchema = true),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PostMapping("/task/{projectId}")
  public ResponseEntity<?> addTask(@PathVariable Long projectId, @RequestBody TaskRequest task) {

    try {
      UserDetails userDetails = getAuthenticatedUserDetails();
      User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
      Project project = getProjectById(projectId);
      verifyUserIsAdmin(logedInUser, project);


      Task newTask = task.toEntity()
              .setProject(project)
              .setStatus("Pending")
              .setLastUpdatedDate(new Date());

      project.getTasks().add(newTask);

      projectService.save(project);
      return ResponseEntity.ok().body(new MessageRes("Task added successfully"));

    } catch (CustomAuthenticationException | CustomAuthorizationException | CustomProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageRes(e.getMessage()));
    }
  }
  @Operation(summary = "Update task in project", description = "Allows an admin to update details of a task within a project")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Task updated successfully", useReturnTypeSchema = true),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Task not found")
  })
  @PostMapping("/task/update/{projectId}/{taskId}")
  public ResponseEntity<?> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody TaskRequest updatedTaskRequest) {
    try {
      UserDetails userDetails = getAuthenticatedUserDetails();
      User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
      Project project = getProjectById(projectId);
      verifyUserIsAdmin(logedInUser, project);


      Optional<Task> taskOptional = project.getTasks().stream()
              .filter(task -> task.getId().equals(taskId))
              .findFirst();

      if (!taskOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageRes("Task not found"));
      }

      Task taskToUpdate = taskOptional.get();

      Task entity = updatedTaskRequest.toEntity();

      taskToUpdate.setTitle(entity.getTitle());
      taskToUpdate.setDescription(entity.getDescription());
      taskToUpdate.setDueDate(entity.getDueDate());
      taskToUpdate.setStatus(entity.getStatus());
      taskToUpdate.setAssignedUser(entity.getAssignedUser());
      taskToUpdate.setLastUpdatedDate(new Date());

      projectService.save(project);
      return ResponseEntity.ok().body(new MessageRes("Task updated successfully"));

    } catch (CustomAuthenticationException | CustomAuthorizationException | CustomProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageRes(e.getMessage()));
    }
  }
  @Operation(summary = "Delete task from project", description = "Allows an admin or the assigned user to delete a task from a project")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Task removed successfully", useReturnTypeSchema = true),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Task not found")
  })
  @DeleteMapping("/task/{projectId}/{taskId}")
  public ResponseEntity<?> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
    try {
      UserDetails userDetails = getAuthenticatedUserDetails();
      User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
      Project project = getProjectById(projectId);

      Optional<Task> taskOptional = project.getTasks().stream()
              .filter(task -> task.getId().equals(taskId))
              .findFirst();

      if (!taskOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageRes("Task not found"));
      }

      Task task = taskOptional.get();

      User assignedUser = task.getAssignedUser();
      if (assignedUser == null)
        verifyUserIsAdmin(logedInUser, project);
      else if (!assignedUser.equals(logedInUser))
        throw new CustomAuthorizationException("User does not have admin permissions for this project or not the assigned user of the task");

      project.getTasks().remove(task);
      projectService.save(project);

      return ResponseEntity.ok().body(new MessageRes("Task removed successfully"));

    } catch (CustomAuthenticationException | CustomAuthorizationException | CustomProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageRes(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<Void> deleteById(Long id) {
    UserDetails userDetails = getAuthenticatedUserDetails();
    User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
    Project project = getProjectById(id);
    verifyUserIsAdmin(logedInUser, project);
    return super.deleteById(id);
  }

  @Override
  public ResponseEntity<Void> deleteBatch(List<Long> ids) {

    UserDetails userDetails = getAuthenticatedUserDetails();
    User logedInUser = userDetailsServiceImpl.findByUsername(userDetails.getUsername());
    for (Long id : ids) {
      Project project = getProjectById(ids.get(0));
      verifyUserIsAdmin(logedInUser, project);
    }
    return super.deleteBatch(ids);
  }

  //Utility methods
  private UserDetails getAuthenticatedUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomAuthenticationException("User is not authenticated");
    }
    Object principal = authentication.getPrincipal();
    if (!(principal instanceof UserDetails)) {
      throw new CustomAuthenticationException("Invalid user details");
    }
    return (UserDetails) principal;
  }


  private Project getProjectById(Long projectId) {
    Optional<Project> optionalProject = projectService.findById(projectId);
    if (optionalProject.isEmpty()) {
      throw new CustomProjectNotFoundException("Project not found");
    }
    return optionalProject.get();
  }

  private void verifyUserIsAdmin(User user, Project project) {
    boolean isAdmin = project.getProjectParticipants().stream()
            .anyMatch(p -> p.getUser().getId().equals(user.getId()) && p.getRoleId().equals("Admin"));
    if (!isAdmin) {
      throw new CustomAuthorizationException("User does not have admin permissions for this project");
    }
  }

}