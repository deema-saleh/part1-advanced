package com.example.craft.repository;


import com.example.craft.models.Task;

import java.util.List;

public interface TaskRepo  extends BaseRepository<Task, Long>  {

  public List<Task> findByAssignedUserUsername(String username);
}
