package com.example.shopping_list.list;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.shopping_list.dto.response.ListDataResponse;
import com.example.shopping_list.dto.response.UserDataResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListResponse extends ListDataResponse {
  private final Set<UserDataResponse> users;

  public ListResponse(List list) {
    super(list.getId(), list.getTitle());
    this.users = list.getUsers()
                   .stream()
                   .map(user -> new UserDataResponse(user.getEmail(), user.getName(), user.getSurname()))
                   .collect(Collectors.toSet());
  }
}
