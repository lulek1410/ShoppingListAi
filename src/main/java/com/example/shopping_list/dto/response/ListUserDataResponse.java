package com.example.shopping_list.dto.response;

import com.example.shopping_list.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ListUserDataResponse extends UserDataResponse {
  private final Long id;

  public ListUserDataResponse(User user) {
    super(user);
    this.id = user.getId();
  }
}
