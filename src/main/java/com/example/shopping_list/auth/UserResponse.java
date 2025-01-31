package com.example.shopping_list.auth;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.shopping_list.dto.response.UserDataResponse;
import com.example.shopping_list.list.List;
import com.example.shopping_list.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserResponse extends UserDataResponse {
  private final Set<ListWithUncheckedItemsCountResponse> lists;

  public UserResponse(User user, Set<List> userLists) {
    super(user);
    this.lists = userLists.stream()
                   .map(list -> {
                     long checkedCount = list.getItems().stream().filter(item -> !item.isChecked()).count();
                     return new ListWithUncheckedItemsCountResponse(list.getId(), list.getTitle(), checkedCount);
                   })
                   .collect(Collectors.toSet());
  }
}
