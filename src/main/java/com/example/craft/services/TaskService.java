package com.example.craft.services;

import com.example.craft.models.Task;
import com.example.craft.repository.BaseRepository;
import com.example.craft.repository.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService extends BaseService<Task, Long> {

  private final TaskRepo taskRepository;

  @Autowired
  public TaskService(TaskRepo taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  protected BaseRepository<Task, Long> getRepository() {
    return taskRepository;
  }

  public List<Task> findTasksByUser(String username) {
    return taskRepository.findByAssignedUserUsername(username);
  }

}