package com.example.shopping_list.auth;

import lombok.Data;

@Data
public class ListResponse {
  private final Long id;
  private final String title;
  private final long uncheckedItem;
}
