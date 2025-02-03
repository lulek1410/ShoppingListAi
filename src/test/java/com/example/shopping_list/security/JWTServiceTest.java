package com.example.shopping_list.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.shopping_list.user.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

  private JWTService jwtService;

  @Mock private User user;

  @BeforeEach
  void setup() {
    jwtService = new JWTService();
    ReflectionTestUtils.setField(
      jwtService,
      "secret",
      "6efd45d7de731080bb083c86ab414582770e349b21fabacbc783769b8cdcfab02c44d4f6338a4c4c2d1846faff5cb6185d7af0173c6bf6612e2e0cdbd4d62ba05ce1548f21655f94e6839bc83279234eb698f8497db0f276c6ae5d2211650eb0a901956ca7a2fd9ef54f55e304b3a1e5151c48f3504704ef9d97bce6c16dac99df97267230b8946f2d298d441b98421f96a83563b4005b03b1b9fadc23c7a760303f94b68f2ce54324e259989deae3117237171fa86465a7eee27419658693cc4fd9c497b7d3bc189e3fcc384f5f90faf0ec44b6eae011b6fe0f0342b2e367ac244958635a015a8e74f9746199d4a17d92c71ff6639ba01475b28807b70478b5");
  }

  @Test
  void testCreateToken() {
    when(user.getId()).thenReturn(1L);
    when(user.getEmail()).thenReturn("test@example.com");
    String token = jwtService.createToken(user);

    assertNotNull(token);
    assertFalse(token.isEmpty());
    assertEquals(3, token.split("\\.").length);

    Long userId = jwtService.generateUserIdFromToken(token);
    assertEquals(1L, userId);
  }

  @Test
  void testGenerateUserIdFromToken_MalformedToken() {
    String malformedTokenToken = "invalid.token.here";
    assertThrows(MalformedJwtException.class, () -> jwtService.generateUserIdFromToken(malformedTokenToken));
  }

  @Test
  void testGenerateUserIdFromToken_EmptyToken() {
    String emptyToken = "";
    assertThrows(IllegalArgumentException.class, () -> jwtService.generateUserIdFromToken(emptyToken));
  }

  @Test
  void testGenerateUserIdFromToken_InvalidToken() {
    String invalidToken =
      "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImlhdCI6MTczODU5MDQwN30.4jHGguqKqgCKytNskf3_Ow8h7tGia5lG0_h1giW8v6s";
    assertThrows(JwtException.class, () -> jwtService.generateUserIdFromToken(invalidToken));
  }
}
