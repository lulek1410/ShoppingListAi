package com.example.shopping_list.auth;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shopping_list.security.JWTService;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;

  public ResponseEntity<Response> login(LoginRequest request) {
    try {
      Authentication auth =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      User user = (User)auth.getPrincipal();
      String token = jwtService.createToken(user);
      return ResponseEntity.ok(new Response(token));
    } catch (BadCredentialsException e) {
      return ResponseEntity.badRequest().body(new Response("Invalid credentials!"));
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().body(new Response("Error during login!"
                                                           + " " + e.getMessage()));
    }
  }

  public ResponseEntity<Response> register(RegistrationRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body(new Response("User already exists!"));
    }
    User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(), request.getSurname());
    User savedUser = userRepository.save(user);
    return ResponseEntity.created(URI.create("/api/users/" + savedUser.getId()))
      .body(new Response("User created successfully: " + savedUser.getName() + " " + savedUser.getSurname()));
  }
}
