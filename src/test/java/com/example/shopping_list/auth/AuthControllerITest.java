package com.example.shopping_list.auth;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shopping_list.dto.request.LoginRequest;
import com.example.shopping_list.dto.request.RegistrationRequest;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;
import com.example.shopping_list.user_list.UserListRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerITest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;
  @Autowired private UserListRepository userListRepository;

  final String email = "mike.example@mail.com";
  final String password = "simplePassphrase123%";
  final String name = "Mike";
  final String surname = "Jones";
  ObjectMapper objectMapper = new ObjectMapper();
  User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User(email, password, name, surname);
    testUser.setId(1L);
  }

  @Nested
  class Login {
    @Test
    void login_shouldReturnOk_whenLoginRequestValid() throws Exception {
      LoginRequest request = new LoginRequest(email, password);

      mockMvc.perform(post("/api/auth/login").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpectAll(jsonPath(".user.id").value(1),
                      jsonPath(".user.email").value(email),
                      jsonPath(".user.name").value(name),
                      jsonPath(".user.surname").value(surname),
                      jsonPath(".user.lists[*].id", containsInAnyOrder(1, 2)),
                      jsonPath(".user.lists[*].title", containsInAnyOrder("Fruits", "Vegetables")),
                      jsonPath(".user.lists[*].uncheckedItem", containsInAnyOrder(2, 1)))
        .andExpect(jsonPath(".token").exists())
        .andExpect(jsonPath(".message").value("Logged in successfully."));
    }

    @SuppressWarnings("unused")
    private static List<Arguments> arguments =
      Arrays.asList(argumentSet("Bad email and password", "josh.bad@mail.com", "simplePassphrase123%"),
                    argumentSet("Bad password", "mike.example@mail.com", "wrongPassword123#"));

    @ParameterizedTest(name = "[{index}] {argumentSetName} | Email: {0}, Password: {1}")
    @FieldSource("arguments")
    void login_shouldReturnBadRequest_whenLoginWithBadCredentials(String email, String password) throws Exception {
      LoginRequest request = new LoginRequest(email, password);

      mockMvc.perform(post("/api/auth/login").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath(".message").value("Invalid credentials!"));
    }
  }

  @Nested
  class Register {
    @Test
    void register_shouldReturnCreated_whenRequestValid() throws Exception {
      final String newEmail = "test.user@mail.com";
      final String newPassword = "Test#123;";
      final String newName = "Josh";
      final String newSurname = "Testman";
      RegistrationRequest request = new RegistrationRequest(newPassword, newEmail, newName, newSurname);

      mockMvc.perform(post("/api/auth/register").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpectAll(jsonPath(".user.id").value(3),
                      jsonPath(".user.email").value(newEmail),
                      jsonPath(".user.name").value(newName),
                      jsonPath(".user.surname").value(newSurname),
                      jsonPath(".user.lists[*].id", containsInAnyOrder()),
                      jsonPath(".user.lists[*].title", containsInAnyOrder()),
                      jsonPath(".user.lists[*].uncheckedItem", containsInAnyOrder()))
        .andExpect(jsonPath(".token").exists())
        .andExpect(jsonPath(".message").value("User registered successfully!"));

      assertTrue(userRepository.existsById(3L));
      assertEquals(0, userListRepository.getListIdsByUserId(3L).size());
    }

    @Test
    void register_shouldReturnBadRequest_whenUserAlreadyExists() throws Exception {

      RegistrationRequest request = new RegistrationRequest("testPass234#", email, "Mario", "Mariaci");

      mockMvc.perform(post("/api/auth/register").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath(".message").value("User already exists!"));
    }
  }
}
