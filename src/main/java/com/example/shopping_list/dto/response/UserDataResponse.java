package com.example.shopping_list.dto.response;

import lombok.Data;

@Data
public class UserDataResponse {
  private final String email;
  private final String name;
  private final String surname;
}
