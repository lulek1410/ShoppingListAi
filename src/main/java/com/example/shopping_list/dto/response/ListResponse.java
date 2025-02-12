package com.example.shopping_list.dto.response;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.shopping_list.list.List;
import com.example.shopping_list.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListResponse extends ListDataResponse {
  private final Set<UserDataResponse> users;

  public ListResponse(List list, Set<User> users) {
    super(list.getId(), list.getTitle());
    this.users = users.stream().map(UserDataResponse::new).collect(Collectors.toSet());
  }
}
