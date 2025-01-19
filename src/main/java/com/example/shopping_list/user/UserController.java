package com.example.shopping_list.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) { this.userService = userService; }
}
