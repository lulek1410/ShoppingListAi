package com.example.shopping_list.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) { this.userRepository = userRepository; }
}
