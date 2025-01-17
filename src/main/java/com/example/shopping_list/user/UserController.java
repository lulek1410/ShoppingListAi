package com.example.shopping_list.user;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping_list.list.ListResponse;
import com.example.shopping_list.list.ListService;

@RestController
@RequestMapping(path = "api/user")
public class UserController {
  private final UserService userService;
  private final ListService listService;

  public UserController(UserService userService, ListService listService) {
    this.userService = userService;
    this.listService = listService;
  }

  @GetMapping("/lists")
  public List<ListResponse> getUserLists(Authentication authentication) {
    final Long userId = userService.getUserIdFromAuthentication(authentication);
    return listService.getListsByUserId(userId);
  }
}
