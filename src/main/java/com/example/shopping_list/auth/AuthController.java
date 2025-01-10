package com.example.shopping_list.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;

  @PostMapping("/api/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    try {
      Authentication auth =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(auth);
      return ResponseEntity.ok(new LoginResponse("Login successful"));
    } catch (BadCredentialsException e) {
      return ResponseEntity.badRequest().body(new LoginResponse("Invalid credentials!"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new LoginResponse("Error during login!"));
    }
  }
}
