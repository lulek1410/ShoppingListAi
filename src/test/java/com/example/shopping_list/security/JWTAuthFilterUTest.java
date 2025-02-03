package com.example.shopping_list.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.shopping_list.security.JWTAuthFilter;
import com.example.shopping_list.security.JWTService;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JWTAuthFilterUTest {

  @Mock private JWTService jwtService;
  @Mock private UserService userService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private User user;

  @InjectMocks private JWTAuthFilter jwtAuthFilter;

  @BeforeEach
  void setup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
    String token = "valid.jwt.token";
    Long userId = 1L;

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtService.generateUserIdFromToken(token)).thenReturn(userId);
    when(userService.getUserById(userId)).thenReturn(user);
    when(user.getAuthorities()).thenReturn(null);

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_WithNoToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
    String token = "invalid.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtService.generateUserIdFromToken(token)).thenReturn(null);

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_UserAlreadyAuthenticated() throws ServletException, IOException {
    String token = "valid.jwt.token";
    Long userId = 1L;
    UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken("existingUser", null, null);
    SecurityContextHolder.getContext().setAuthentication(existingAuth);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtService.generateUserIdFromToken(token)).thenReturn(userId);

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
