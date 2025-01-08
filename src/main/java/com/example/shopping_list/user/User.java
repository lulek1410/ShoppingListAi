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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "user_email_unique", columnNames = { "email" }) })
@Setter
@Getter
@RequiredArgsConstructor
@ToString
public class User {
  @Id
  @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
  @Column(nullable = false, updatable = false)
  private final Long id;

  @Column(nullable = false, columnDefinition = "TEXT") private final String username;

  @Column(nullable = false, columnDefinition = "TEXT") private final String password;

  @Column(nullable = false, columnDefinition = "TEXT") private final String email;
}
