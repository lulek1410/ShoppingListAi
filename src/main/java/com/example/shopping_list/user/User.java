package com.example.shopping_list.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "user_email_unique", columnNames = { "email" }) })
@Setter
@Getter
@ToString
public class User {
  @Id
  @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT") private String username;

  @Column(nullable = false, columnDefinition = "TEXT") private String password;

  @Column(nullable = false, columnDefinition = "TEXT") private String email;

  User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }
}
