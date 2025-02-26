package com.example.shopping_list.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.shopping_list.dto.request.LoginRequest;
import com.example.shopping_list.dto.request.RegistrationRequest;
import com.example.shopping_list.dto.response.LoginResponse;
import com.example.shopping_list.dto.response.Response;
import com.example.shopping_list.list.List;
import com.example.shopping_list.security.JWTService;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;
import com.example.shopping_list.user_list.UserListRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceUTest {

  @Mock AuthenticationManager authenticationManager;
  @Mock Authentication authentication;
  @Mock JWTService jwtService;
  @Mock UserListRepository userListRepository;
  @Mock UserRepository userRepository;
  @Mock PasswordEncoder passwordEncoder;
  @InjectMocks AuthService authService;

  User testUser;
  Set<List> testLists;

  @BeforeEach
  void setup() {
    testUser = new User("test@mail.com", "password_test123", "John", "Doe");
    testUser.setId(1L);

    testLists = Set.of(new List("first list name"), new List("Second list title test"));
  }

  @Nested
  class Login {
    @Test
    void login_shouldReturnLoginResponse_whenLoginRequestValid() {
      final String testEmail = "test@mail.com";
      final String testPassword = "password_test123";
      final String testToken = "mockToken";
      final LoginRequest testLoginRequest = new LoginRequest(testEmail, testPassword);

      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(jwtService.createToken(testUser)).thenReturn(testToken);
      when(userListRepository.getListsByUserId(testUser.getId())).thenReturn(testLists);

      ResponseEntity<Response> responseEntity = authService.login(testLoginRequest);

      assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      assertInstanceOf(LoginResponse.class, responseEntity.getBody());

      LoginResponse loginResponse = (LoginResponse)responseEntity.getBody();
      assertNotNull(loginResponse);
      assertEquals(testToken, loginResponse.getToken());
      assertEquals("Logged in successfully.", loginResponse.getMessage());
      assertEquals(2, loginResponse.getUser().getLists().size());

      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(jwtService).createToken(testUser);
      verify(userListRepository).getListsByUserId(testUser.getId());
    }

    @Test
    void login_shouldReturnBadRequest_whenCredentialsInvalid() {
      LoginRequest request = new LoginRequest("test@example.com", "wrong_password");

      when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

      ResponseEntity<Response> responseEntity = authService.login(request);

      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Response responseBody = responseEntity.getBody();
      assertNotNull(responseBody);
      assertEquals("Invalid credentials!", responseBody.getMessage());

      verify(authenticationManager).authenticate(any());
      verifyNoInteractions(jwtService, userListRepository);
    }
  }

  @Nested
  class Register {
    RegistrationRequest request = new RegistrationRequest("test@mail.com", "password", "John", "Doe");

    @Test
    void register_shouldReturnBadRequest_whenUserAlreadyExists() {
      when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

      ResponseEntity<Response> response = authService.register(request);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Response responseBody = response.getBody();
      assertNotNull(responseBody);
      assertEquals("User already exists!", responseBody.getMessage());

      verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldReturnCreated_whenRegistrationRequestValid() {
      User savedUser = new User("test@mail.com", "encodedPassword", "John", "Doe");
      savedUser.setId(1L);

      when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
      when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
      when(userRepository.save(any(User.class))).thenReturn(savedUser);

      ResponseEntity<Response> response = authService.register(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      Response responseBody = response.getBody();
      assertNotNull(responseBody);
      assertEquals("User registered successfully!", responseBody.getMessage());

      verify(userRepository).save(any(User.class));
      verify(passwordEncoder).encode(request.getPassword());
    }
  }
}
