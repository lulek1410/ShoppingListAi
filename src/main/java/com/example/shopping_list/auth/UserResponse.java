package com.example.shopping_list.auth;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.shopping_list.user.User;

import lombok.Data;

@Data
public class UserResponse {
  private final String email;
  private final String name;
  private final String surname;
  private final Set<ListResponse> lists;

  public UserResponse(User user) {
    this.email = user.getEmail();
    this.name = user.getName();
    this.surname = user.getSurname();
    this.lists = user.getLists().stream().map(list -> new ListResponse(list.getId(), list.getTitle())).collect(Collectors.toSet());
  }
}
