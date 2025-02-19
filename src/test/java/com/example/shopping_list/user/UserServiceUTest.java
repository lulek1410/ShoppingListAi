package com.example.shopping_list.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.shopping_list.dto.exception.ResourceNotFoundException;
import com.example.shopping_list.list.List;
import com.example.shopping_list.list.ListRepository;
import com.example.shopping_list.user_list.UserListId;
import com.example.shopping_list.user_list.UserListRepository;
import com.example.shopping_list.web_socket.RoomService;

@ExtendWith(MockitoExtension.class)
class UserServiceUTest {

  @Mock private UserRepository userRepository;
  @Mock private UserListRepository userListRepository;
  @Mock private ListRepository listRepository;
  @Mock private RoomService roomService;
  @Mock private Authentication authentication;

  @InjectMocks private UserService userService;

  private User testUser;
  private List testList;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "password", "John", "Doe");
    testList = new List("test list title");
    testUser.setId(2L);
    testList.setId(1L);

    authentication = new UsernamePasswordAuthenticationToken(testUser, null);
  }

  @Nested
  class LoadUserByUsername {
    final String email = "test@example.com";

    @Test
    void loadUserByUsername_shouldReturnUser_whenUserExists() {
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

      UserDetails userDetails = userService.loadUserByUsername(email);

      assertNotNull(userDetails);
      assertEquals(testUser.getEmail(), userDetails.getUsername());
      assertEquals(testUser.getPassword(), userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserNotFound() {
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));

      assertEquals("User not found", exception.getMessage());
    }
  }

  @Nested
  class GetUserById {
    Long userId = 1L;

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
      when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

      User user = userService.getUserById(userId);

      assertNotNull(user);
      assertEquals(testUser, user);
    }

    @Test
    void getUserById_shouldThrowResourceNotFoundException_whenUserNotFound() {
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> { userService.getUserById(userId); });

      assertEquals("User not found", exception.getMessage());
    }
  }

  @Nested
  class RemoveList {
    final Long listId = 1L;

    static Stream<Arguments> provideListData() { return Stream.of(Arguments.of(Set.of(2L, 3L), false), Arguments.of(Set.of(2L), true)); }

    @ParameterizedTest(name = "Remove list with users {0}, should delete list: {1}")
    @MethodSource("provideListData")
    void removeList_shouldRemoveList_whenUserAssignedToList(Set<Long> userIds, boolean shouldDeleteList) {
      final Long userId = 2L;

      when(listRepository.findById(listId)).thenReturn(Optional.of(testList));
      when(userListRepository.getUserIdsByListId(listId)).thenReturn(userIds);

      userService.removeList(listId, authentication);

      verify(userListRepository).deleteById(new UserListId(userId, listId));
      verify(roomService).removeUserFromRoom(listId, userId);
      verify(roomService).notifyRoom(eq(listId), eq(userId), any());

      if (shouldDeleteList) {
        verify(listRepository).delete(testList);
      } else {
        verify(listRepository, never()).delete(testList);
      }
    }

    @Test
    void removeList_shouldThrowAccessDeniedException_whenUserNotAssignedToList() {
      when(listRepository.findById(listId)).thenReturn(Optional.of(testList));
      when(userListRepository.getUserIdsByListId(listId)).thenReturn(Set.of(1L));

      AccessDeniedException exception =
        assertThrows(AccessDeniedException.class, () -> { userService.removeList(listId, authentication); });

      assertEquals("Can't remove list. List does not belong to you", exception.getMessage());
    }

    @Test
    void removeList_shouldThrowResourceNotFoundException_whenListNotFound() {
      when(listRepository.findById(listId)).thenReturn(Optional.empty());

      ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> { userService.removeList(listId, authentication); });

      assertEquals("List not found", exception.getMessage());
    }

    @Test
    void removeList_shouldThrowUsernameNotFoundException_whenUserNotFound() {
      String email = "nonexistent@example.com";

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> { userService.loadUserByUsername(email); });

      assertEquals("User not found", exception.getMessage());
    }
  }
}
