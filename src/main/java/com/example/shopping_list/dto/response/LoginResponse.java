package com.example.shopping_list.dto.response;

import java.util.Set;

import com.example.shopping_list.list.List;
import com.example.shopping_list.user.User;

import lombok.Data;

@Data
public class LoginResponse {
  private final UserResponse user;
  private final String token;

  public LoginResponse(User user, Set<List> userLists, String token) {
    this.user = new UserResponse(user, userLists);
    this.token = token;
  }
}
