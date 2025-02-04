package com.example.shopping_list.dto.response;

import java.util.Set;

import com.example.shopping_list.list.List;
import com.example.shopping_list.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginResponse extends Response {
  private final UserResponse user;
  private final String token;

  public LoginResponse(User user, Set<List> userLists, String token, String message) {
    super(message);
    this.user = new UserResponse(user, userLists);
    this.token = token;
  }

  public LoginResponse(LoginResponse loginResponse, String message) {
    super(message);
    this.user = loginResponse.getUser();
    this.token = loginResponse.getToken();
  }
}
