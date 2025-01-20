package com.example.shopping_list.dto.response;

import com.example.shopping_list.user.User;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDataResponse {
  private final String email;
  private final String name;
  private final String surname;

  public UserDataResponse(User user) {
    this.email = user.getEmail();
    this.name = user.getName();
    this.surname = user.getSurname();
  }
}
