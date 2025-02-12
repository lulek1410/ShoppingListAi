package com.example.shopping_list.list;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shopping_list.dto.request.AddListItemRequest;
import com.example.shopping_list.dto.request.CreateListRequest;
import com.example.shopping_list.list_item.ListItemRepository;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user_list.UserListRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ListControllerITest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ListRepository listRepository;
  @Autowired private UserListRepository userListRepository;
  @Autowired private ListItemRepository listItemRepository;

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
  class GetList {
    @Test
    void getList_shouldReturnOk_whenSuccess() throws Exception {
      mockMvc.perform(get("/api/list/{listId}", 2).with(user(testUser)))
        .andExpect(status().isOk())
        .andExpectAll(jsonPath("$.users").isArray(),
                      jsonPath("$.users[*].id", containsInAnyOrder(1, 2)),
                      jsonPath("$.users[*].email", containsInAnyOrder("mike.example@mail.com", "ann.mirco@mail.com")),
                      jsonPath("$.users[*].name", containsInAnyOrder("Mike", "Angelika")),
                      jsonPath("$.users[*].surname", containsInAnyOrder("Jones", "Mirco")))
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.title").value("Vegetables"));
    }

    @Test
    void getList_shouldReturnForbidden_whenListNotAssignedToUser() throws Exception {
      mockMvc.perform(get("/api/list/{listId}", 3).with(user(testUser)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Requested list does not belong to you"));
    }

    @Test
    void getList_shouldReturnNotFound_whenListNotFound() throws Exception {
      mockMvc.perform(get("/api/list/{listId}", 100).with(user(testUser)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("List with id: 100 does not exist!"));
    }
  }

  @Nested
  class CreateList {
    @Test
    void createList_shouldReturnOk_whenSuccess() throws Exception {
      final String title = "new list to be added";
      CreateListRequest request = new CreateListRequest(title, 3);

      mockMvc
        .perform(post("/api/list/create")
                   .content(objectMapper.writeValueAsString(request))
                   .contentType(MediaType.APPLICATION_JSON)
                   .with(user(testUser)))
        .andExpect(status().isCreated())
        .andExpectAll(jsonPath("$.users").isArray(),
                      jsonPath("$.users[*].id", contains(1)),
                      jsonPath("$.users[*].email", contains(email)),
                      jsonPath("$.users[*].name", contains(name)),
                      jsonPath("$.users[*].surname", contains(surname)))
        .andExpect(jsonPath("$.id").value(4L))
        .andExpect(jsonPath("$.title").value(title));

      assertTrue(listRepository.existsById(4L));
      Set<Long> userIds = userListRepository.getUserIdsByListId(4L);
      assertEquals(1, userIds.size());
      assertTrue(userIds.contains(1L));
    }
  }

  @Nested
  class AddItem {
    final String content = "new list item";
    final int order = 1;

    @Test
    void addItem_shouldReturnOk_whenSuccess() throws Exception {
      final Long listId = 1L;
      AddListItemRequest request = new AddListItemRequest(listId, content, order);

      final Long newItemId = 5L;

      mockMvc
        .perform(post("/api/list/addItem")
                   .content(objectMapper.writeValueAsString(request))
                   .contentType(MediaType.APPLICATION_JSON)
                   .with(user(testUser)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(newItemId))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.checked").value(false))
        .andExpect(jsonPath("$.itemOrder").value(order));

      assertTrue(listItemRepository.existsById(newItemId));
      Set<Long> userIds = userListRepository.getUserIdsByListId(listId);
      assertEquals(1, userIds.size());
      assertTrue(userIds.contains(1L));
    }

    @Test
    void addItem_shouldReturnNotFound_whenListDoesNotExist() throws Exception {
      final int initialSize = listItemRepository.findAll().size();
      final Long listId = 100L;
      AddListItemRequest request = new AddListItemRequest(listId, content, order);

      mockMvc
        .perform(post("/api/list/addItem")
                   .content(objectMapper.writeValueAsString(request))
                   .contentType(MediaType.APPLICATION_JSON)
                   .with(user(testUser)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("List with id: 100 does not exist!"));

      assertEquals(initialSize, listItemRepository.findAll().size());
    }
  }
}
