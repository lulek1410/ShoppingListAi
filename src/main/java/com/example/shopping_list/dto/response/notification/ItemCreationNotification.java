package com.example.shopping_list.dto.response.notification;

import com.example.shopping_list.list_item.ListItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemCreationNotification extends Notification {
  private final ListItem listItem;

  public ItemCreationNotification(String message, ListItem listItem) {
    super(message, NotificationType.LIST_ITEM_ADDED);
    this.listItem = listItem;
  }
}
