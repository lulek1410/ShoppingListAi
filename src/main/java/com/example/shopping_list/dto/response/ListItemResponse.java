package com.example.shopping_list.dto.response;

import com.example.shopping_list.list_item.ListItem;

import lombok.Data;

@Data
public class ListItemResponse {
  private final Long id;
  private final String content;
  private final boolean checked;
  private final int itemOrder;

  public ListItemResponse(ListItem listItem) {
    this.id = listItem.getId();
    this.content = listItem.getContent();
    this.checked = listItem.isChecked();
    this.itemOrder = listItem.getItemOrder();
  }
}
