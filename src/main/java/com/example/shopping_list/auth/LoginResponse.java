package com.example.shopping_list.auth;

import com.example.shopping_list.user.User;

import lombok.Data;

@Data
public class LoginResponse {
  private final UserResponse user;
  private final String token;

  public LoginResponse(User user, String token) {
    this.user = new UserResponse(user);
    this.token = token;
  }
}
