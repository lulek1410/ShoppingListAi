package com.example.shopping_list.auth;

import lombok.Data;

@Data
public class RegistrationRequest {
  private final String email;
  private final String password;
  private final String name;
  private final String surname;
}
