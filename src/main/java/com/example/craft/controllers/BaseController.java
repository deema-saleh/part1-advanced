package com.example.craft.controllers;

import com.example.craft.payloads.request.AbstractRequest;
import com.example.craft.payloads.response.AbstractResponse;
import com.example.craft.services.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:9090"}, maxAge = 3600)
@SecurityRequirement(name = "Bearer Authentication")
public abstract class BaseController<T, RES extends AbstractRequest<T>, RESP extends AbstractResponse<T, RESP>, ID> {

  protected abstract BaseService<T, ID> getService();
  @Operation(summary = "Get all entities", description = "Retrieves a list of all entities")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
  })
  @GetMapping
  public ResponseEntity<List<RESP>> getAll() {
    return ResponseEntity.ok(getService().findAll().stream()
            .map(x -> getResp(x))
            .toList());
  }
  @Operation(summary = "Get entity by ID", description = "Retrieves a single entity by its ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved entity"),
          @ApiResponse(responseCode = "404", description = "Entity not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<RESP> getById(@PathVariable ID id) {
    return getService().findById(id)
            .map(x -> getResp(x))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }
  @Operation(summary = "Create a new entity", description = "Creates a new entity with the provided data")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entity created successfully")
  })
  @PostMapping
  public ResponseEntity<RESP> create(@RequestBody RES entity) {
    T entityEntity = entity.toEntity();
    T savedEntity = getService().save(entityEntity);
    RESP resp = getResp(savedEntity);
    return ResponseEntity.ok(resp);
  }
  @Operation(summary = "Create entities in batch", description = "Creates multiple entities with the provided data in a single operation")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entities created successfully")
  })
  @PostMapping("/batch")
  public ResponseEntity<List<RESP>> createBatch(@RequestBody List<RES> entities) {
    List<T> list = entities.stream().map(RES::toEntity).toList();
    List<RESP> savedEntities = getService().saveAll(list).stream().map(x -> getResp(x)).toList();
    return ResponseEntity.ok(savedEntities);
  }
  @Operation(summary = "Delete entity by ID", description = "Deletes a single entity by its ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entity deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Entity not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable ID id) {
    getService().deleteById(id);
    return ResponseEntity.ok().build();
  }
  @Operation(summary = "Delete entities in batch", description = "Deletes multiple entities by their IDs in a single operation")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entities deleted successfully")
  })
  @DeleteMapping("/batch")
  public ResponseEntity<Void> deleteBatch(@RequestBody List<ID> ids) {
    ids.forEach(getService()::deleteById);
    return ResponseEntity.ok().build();
  }

  private RESP getResp(T x) {
    try {
      RESP instance = (RESP) ((Class) ((ParameterizedType) this.getClass().
              getGenericSuperclass()).getActualTypeArguments()[2]).newInstance();
      RESP resp = instance.toResp(x);
      return resp;
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}