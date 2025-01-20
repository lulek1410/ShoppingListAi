package com.example.shopping_list.auth;

import com.example.shopping_list.dto.response.ListDataResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListWithUncheckedItemsCountResponse extends ListDataResponse {
  private final Long uncheckedItem;

  public ListWithUncheckedItemsCountResponse(Long id, String title, Long uncheckedItem) {
    super(id, title);
    this.uncheckedItem = uncheckedItem;
  }
}
