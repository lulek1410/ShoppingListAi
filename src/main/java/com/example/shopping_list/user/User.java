package com.example.shopping_list.user;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "user_email_unique", columnNames = { "email" }) })
@Setter
@Getter
@ToString
@NoArgsConstructor
public class User implements UserDetails {
  @Id
  @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT") private String email;
  @Column(nullable = false, columnDefinition = "TEXT") private String password;
  @Column(nullable = false, columnDefinition = "TEXT") private String name;
  @Column(nullable = false, columnDefinition = "TEXT") private String surname;
  @Column(nullable = false, columnDefinition = "TIMESTAMP", updatable = false) @JsonIgnore private Timestamp creationDate;

  public User(String email, String password, String name, String surname) {
    this.name = name;
    this.surname = surname;
    this.password = password;
    this.email = email;
    this.creationDate = Timestamp.from(new Date().toInstant());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList(); // or return roles/authorities if you have any
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
