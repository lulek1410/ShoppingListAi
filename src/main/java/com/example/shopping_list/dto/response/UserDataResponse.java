package com.example.shopping_list.dto.response;

import com.example.shopping_list.dto.UserData;
import com.example.shopping_list.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDataResponse extends UserData {
  private final Long id;

  public UserDataResponse(User user) {
    super(user.getEmail(), user.getName(), user.getSurname());
    this.id = user.getId();
  }
}
