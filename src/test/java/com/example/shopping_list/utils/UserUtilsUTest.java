package com.example.shopping_list.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.shopping_list.user.User;

@ExtendWith(MockitoExtension.class)
class UserUtilsUTest {

  @Test
  void testGetUserFromAuthentication() {
    User user = new User("john.dow@mail.com", "jd123%^", "John", "Doe");
    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

    User extractedUser = UserUtils.getUserFromAuthentication(authentication);

    assertNotNull(extractedUser);
    assertEquals(user, extractedUser);
  }
}
