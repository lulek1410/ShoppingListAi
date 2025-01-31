package com.example.shopping_list.utils;

import org.springframework.security.core.Authentication;

import com.example.shopping_list.user.User;

public final class UserUtils {

  private UserUtils() { throw new UnsupportedOperationException("Utility class should not be instantiated"); }

  public static User getUserFromAuthentication(Authentication authentication) {
    return authentication.getPrincipal() instanceof User user ? user : null;
  }
}