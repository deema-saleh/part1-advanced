package com.example.craft.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Accessors(chain = true)
public class Task extends BaseDTO {
  private String title;
  @Column(length = 5000)
  private String description;
  private Date dueDate;
  private String status;
  private Date lastUpdatedDate;

  @ManyToOne
  @JoinColumn(name = "projectId")
  private Project project;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User assignedUser;

}