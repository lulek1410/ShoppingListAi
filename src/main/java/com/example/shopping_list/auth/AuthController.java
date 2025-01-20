package com.example.shopping_list.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shopping_list.dto.response.Response;

import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<Object> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    return authService.login(request);
  }

  @PostMapping("/register")
  public ResponseEntity<Response> register(@RequestBody RegistrationRequest request) {
    return authService.register(request);
  }
}
