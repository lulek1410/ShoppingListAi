package com.example.shopping_list.user;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.shopping_list.list.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
  @Column(nullable = false, columnDefinition = "TEXT") @JsonIgnore private String password;
  @Column(nullable = false, columnDefinition = "TEXT") private String name;
  @Column(nullable = false, columnDefinition = "TEXT") private String surname;
  @Column(nullable = false, columnDefinition = "TIMESTAMP", updatable = false) @JsonIgnore private Timestamp creationDate;

  @ManyToMany(mappedBy = "users") private Set<List> lists = new HashSet<>();

  public User(String email, String password, String name, String surname) {
    this.name = name;
    this.surname = surname;
    this.password = password;
    this.email = email;
    this.creationDate = Timestamp.from(new Date().toInstant());
  }

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return email;
  }
}
