package com.example.craft.services;

import com.example.craft.models.Project;
import com.example.craft.repository.BaseRepository;
import com.example.craft.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends BaseService<Project, Long> {

  private final ProjectRepository projectRepository;

  @Autowired
  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  protected BaseRepository<Project, Long> getRepository() {
    return projectRepository;
  }

  // Additional project-specific methods can be implemented here.
}