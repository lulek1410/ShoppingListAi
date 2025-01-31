package com.example.shopping_list.dto.request;

import lombok.Data;

@Data
public class CreateListRequest {
  private final String title;
  private final int order;
}
