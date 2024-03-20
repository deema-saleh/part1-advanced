package com.example.craft.services;

import com.example.craft.repository.BaseRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> {

  protected abstract BaseRepository<T, ID> getRepository();

  @Transactional(readOnly = true)
  public List<T> findAll() {
    return getRepository().findAll();
  }

  @Transactional(readOnly = true)
  public Optional<T> findById(ID id) {
    return getRepository().findById(id);
  }

  @Transactional
  public T save(T entity) {
    return getRepository().save(entity);
  }

  @Transactional
  public List<T> saveAll(Iterable<T> entities) {
    return getRepository().saveAll(entities);
  }

  @Transactional
  public void deleteById(ID id) {
    getRepository().deleteById(id);
  }

  @Transactional
  public void deleteAll(Iterable<T> entities) {
    getRepository().deleteAll(entities);
  }
}