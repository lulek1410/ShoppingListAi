package com.example.shopping_list.list;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.shopping_list.dto.response.ListDataResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListResponse extends ListDataResponse {
  private final Set<ListUserDataResponse> users;

  public ListResponse(List list) {
    super(list.getId(), list.getTitle());
    this.users = list.getUsers().stream().map(ListUserDataResponse::new).collect(Collectors.toSet());
  }
}
