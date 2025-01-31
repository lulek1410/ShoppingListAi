package com.example.shopping_list.list;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import jakarta.persistence.EntityNotFoundException;

@Service
public class ListService {
  private final ListRepository listRepository;
  private final ListItemRepository listItemRepository;
  private final UserListRepository userListRepository;
  private final UserRepository userRepository;
  private final RoomService roomService;

  public ListService(ListRepository listRepository,
                     RoomService roomService,
                     ListItemRepository listItemRepository,
                     UserListRepository userListRepository,
                     UserRepository userRepository) {
    this.listRepository = listRepository;
    this.roomService = roomService;
    this.listItemRepository = listItemRepository;
    this.userListRepository = userListRepository;
    this.userRepository = userRepository;
  }

  public ListResponse getListData(Long id, Authentication auth) throws EntityNotFoundException, AccessDeniedException {
    final List list =
      listRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("List with id: " + id + " does not exist!"));
    final Long userId = UserUtils.getUserFromAuthentication(auth).getId();
    Set<Long> listUsersIds = userListRepository.getUserIdsByListId(list.getId());

    if (!listUsersIds.contains(userId)) {
      throw new AccessDeniedException("Requested list does not belong to you");
    }

    Set<User> users = userRepository.findAllById(listUsersIds).stream().collect(Collectors.toSet());
    return new ListResponse(list, users);
  }

  @Transactional
  public void createList(CreateListRequest req, Authentication auth) {
    final User user = UserUtils.getUserFromAuthentication(auth);
    final List newList = new List(req.getTitle());
    final List savedList = listRepository.save(newList);
    userListRepository.save(new UserList(user, savedList, req.getOrder()));
  }

  @Transactional
  public void addItem(AddListItem req, Authentication auth) {
    final Long listId = req.getListId();
    final List list =
      listRepository.findById(listId).orElseThrow(() -> new ResourceNotFoundException("List with id: " + listId + " does not exist!"));

    final String itemContent = req.getContent();
    final ListItem newListItem = new ListItem(list, itemContent, req.getOrder());
    final ListItem savedItem = listItemRepository.save(newListItem);

    list.getItems().add(newListItem);
    listRepository.save(list);

    final Long userId = UserUtils.getUserFromAuthentication(auth).getId();
    final String itemContentMessage = itemContent.length() > 20 ? itemContent.substring(0, 20) + "..." : itemContent;
    final String notificationMessage = "New Item "
                                       + "\"" + itemContentMessage + "\""
                                       + " in list " + list.getTitle();
    roomService.notifyRoom(listId, userId, new ItemCreationNotification(notificationMessage, savedItem));
  }
}
