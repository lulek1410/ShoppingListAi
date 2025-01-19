package com.example.shopping_list.list;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.shopping_list.user.User;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lists")
@Setter
@Getter
@RequiredArgsConstructor
public class List implements Serializable {
  @Id
  @SequenceGenerator(name = "list_sequence", sequenceName = "list_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_sequence")
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT") private String title;

  @ManyToMany
  @JoinTable(name = "table_users", joinColumns = @JoinColumn(name = "list_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  @JsonManagedReference
  private Set<User> users = new HashSet<>();

  public List(String title, Set<User> users) {
    this.title = title;
    this.users = users;
  }
}
