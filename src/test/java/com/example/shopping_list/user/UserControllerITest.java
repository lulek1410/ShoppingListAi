package com.example.shopping_list.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerITest {

  @Autowired private MockMvc mockMvc;

  @Test
  void removeList_shouldReturnOk_whenSuccess() throws Exception {
    User testUser = new User("testuser1@example.com", "password1", "John", "Doe");
    testUser.setId(1L);

    mockMvc.perform(delete("/api/user/removeList/{listId}", 1).with(user(testUser)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("List removed."));
  }

  @Test
  void removeList_shouldReturnForbidden_whenAccessDenied() throws Exception {
    User testUser = new User("testuser1@example.com", "password1", "John", "Doe");
    testUser.setId(1L);

    mockMvc.perform(delete("/api/user/removeList/{listId}", 3).with(user(testUser)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.message").value("Can't remove list. List does not belong to you"));
  }

  @Test
  void removeList_shouldReturnNotFound_whenResourceNotFound() throws Exception {
    User testUser = new User("testuser1@example.com", "password1", "John", "Doe");
    testUser.setId(1L);

    mockMvc.perform(delete("/api/user/removeList/{listId}", 1000).with(user(testUser)))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("List not found"));
  }
}