package com.example.shopping_list.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
  private final String email;
  private final String password;
}
