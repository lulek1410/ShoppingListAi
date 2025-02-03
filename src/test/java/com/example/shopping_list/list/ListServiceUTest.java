package com.example.shopping_list.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.shopping_list.dto.exception.ResourceNotFoundException;
import com.example.shopping_list.dto.request.AddListItem;
import com.example.shopping_list.dto.request.CreateListRequest;
import com.example.shopping_list.dto.response.notification.ItemCreationNotification;
import com.example.shopping_list.list_item.ListItem;
import com.example.shopping_list.list_item.ListItemRepository;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;
import com.example.shopping_list.user_list.UserList;
import com.example.shopping_list.user_list.UserListRepository;
import com.example.shopping_list.utils.UserUtils;
import com.example.shopping_list.web_socket.RoomService;

@ExtendWith(MockitoExtension.class)
class ListServiceUTest {
  @Mock ListRepository listRepository;
  @Mock UserListRepository userListRepository;
  @Mock UserRepository userRepository;
  @Mock ListItemRepository listItemRepository;
  @Mock RoomService roomService;
  @Mock Authentication authentication;

  @InjectMocks ListService listService;

  private User testUser;
  private List testList;

  @BeforeEach
  void setup() {
    testUser = new User("test@mail.com", "userPassword", "John", "Doe");
    testList = new List("test list name");
    testUser.setId(1L);
    testList.setId(1L);

    authentication = new UsernamePasswordAuthenticationToken(testUser, null);
  }

  @Test
  void getListData_ValidCase() {
    final Long listId = testList.getId();
    final Set<Long> userIds = Set.of(1L);
    final java.util.List<User> users = java.util.List.of(testUser, new User("aditional@mail.com", "pass", "Michael", "Doe"));

    when(listRepository.findById(listId)).thenReturn(Optional.of(testList));
    when(userListRepository.getUserIdsByListId(listId)).thenReturn(userIds);
    when(userRepository.findAllById(userIds)).thenReturn(users);

    ListResponse listResponse = listService.getListData(listId, authentication);

    assertEquals(listResponse, new ListResponse(testList, users.stream().collect(Collectors.toSet())));
  }

  @Test
  void getListData_ListNotBelongToUser() {
    final Long listId = testList.getId();
    final Set<Long> userIds = Set.of(2L);

    when(listRepository.findById(listId)).thenReturn(Optional.of(testList));
    when(userListRepository.getUserIdsByListId(listId)).thenReturn(userIds);

    AccessDeniedException error = assertThrows(AccessDeniedException.class, () -> listService.getListData(listId, authentication));

    assertEquals("Requested list does not belong to you", error.getMessage());
  }

  @Test
  void getListData_ListNotFound() {
    final Long listId = testList.getId();

    when(listRepository.findById(listId)).thenReturn(Optional.empty());

    ResourceNotFoundException error = assertThrows(ResourceNotFoundException.class, () -> listService.getListData(listId, authentication));

    assertEquals("List with id: " + listId + " does not exist!", error.getMessage());
  }

  @Test
  void createList() {
    final CreateListRequest request = new CreateListRequest("new list title", 0);
    final List newList = new List(request.getTitle());
    newList.setId(2L);

    try (var mockedUserUtils = mockStatic(UserUtils.class)) {
      mockedUserUtils.when(() -> UserUtils.getUserFromAuthentication(authentication)).thenReturn(testUser);

      when(listRepository.save(any(List.class))).thenReturn(newList);

      listService.createList(request, authentication);

      verify(listRepository).save(any(List.class));
      verify(userListRepository).save(any(UserList.class));
    }
  }

  @Test
  void addItem() {
    final Long listId = testList.getId();
    final String itemContent = "New item content";
    final AddListItem request = new AddListItem(listId, itemContent, 0);

    final ListItem newListItem = new ListItem(testList, itemContent, request.getOrder());
    newListItem.setId(2L);

    try (var mockedUserUtils = mockStatic(UserUtils.class)) {
      mockedUserUtils.when(() -> UserUtils.getUserFromAuthentication(authentication)).thenReturn(testUser);

      when(listRepository.findById(listId)).thenReturn(Optional.of(testList));
      when(listItemRepository.save(any(ListItem.class))).thenReturn(newListItem);

      listService.addItem(request, authentication);

      ArgumentCaptor<ListItem> listItemCaptor = ArgumentCaptor.forClass(ListItem.class);
      verify(listItemRepository).save(listItemCaptor.capture());
      ListItem capturedListItem = listItemCaptor.getValue();

      assertEquals(itemContent, capturedListItem.getContent());
      assertEquals(testList, capturedListItem.getList());
      assertEquals(request.getOrder(), capturedListItem.getItemOrder());

      verify(listRepository).save(testList);

      ArgumentCaptor<ItemCreationNotification> notificationCaptor = ArgumentCaptor.forClass(ItemCreationNotification.class);
      verify(roomService).notifyRoom(eq(listId), eq(testUser.getId()), notificationCaptor.capture());

      ItemCreationNotification capturedNotification = notificationCaptor.getValue();

      String expectedMessage =
        "New Item \"" + itemContent.substring(0, Math.min(itemContent.length(), 20)) + "\" in list " + testList.getTitle();
      assertEquals(expectedMessage, capturedNotification.getMessage());
    }
  }
}
