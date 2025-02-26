package com.example.shopping_list.auth;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shopping_list.dto.request.LoginRequest;
import com.example.shopping_list.dto.request.RegistrationRequest;
import com.example.shopping_list.dto.response.LoginResponse;
import com.example.shopping_list.dto.response.Response;

import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<Response> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    return authService.login(request);
  }

  @PostMapping("/register")
  public ResponseEntity<Response> register(@RequestBody RegistrationRequest request) {
    ResponseEntity<Response> response = authService.register(request);
    if (!response.getStatusCode().is2xxSuccessful()) {
      return response;
    }
    ResponseEntity<Response> loginResponse = authService.login(new LoginRequest(request.getEmail(), request.getPassword()));
    if (!loginResponse.getStatusCode().is2xxSuccessful()) {
      return ResponseEntity.internalServerError().body(new Response("User created but cant be logged in."));
    }

    LoginResponse loginResponseBody = (LoginResponse)loginResponse.getBody();
    if (loginResponseBody == null) {
      return ResponseEntity.internalServerError().body(new Response("User created but cant be logged in. Login data empty."));
    }

    LoginResponse registerResponse = new LoginResponse(loginResponseBody, "User registered successfully!");
    return ResponseEntity.created(URI.create("/api/users/" + loginResponseBody.getUser().getId())).body(registerResponse);
  }
}
