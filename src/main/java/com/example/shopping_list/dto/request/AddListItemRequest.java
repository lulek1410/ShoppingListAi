package com.example.shopping_list.dto.request;

import lombok.Data;

@Data
public class AddListItemRequest {
  private final Long listId;
  private final String content;
  private final int order;
}
