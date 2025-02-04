package com.example.shopping_list.dto.request;

import com.example.shopping_list.dto.UserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationRequest extends UserData {
  private final String password;

  public RegistrationRequest(String password, String email, String name, String surname) {
    super(email, name, surname);
    this.password = password;
  }
}
