package com.example.shopping_list.list;

import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.shopping_list.dto.exception.ResourceNotFoundException;
import com.example.shopping_list.dto.request.AddListItem;
import com.example.shopping_list.dto.response.notification.ItemCreationNotification;
import com.example.shopping_list.dto.response.notification.Notification;
import com.example.shopping_list.list_item.ListItem;
import com.example.shopping_list.list_item.ListItemRepository;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserService;
import com.example.shopping_list.web_socket.RoomService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ListService {
  private final ListRepository listRepository;
  private final ListItemRepository listItemRepository;
  private final UserService userService;
  private final RoomService roomService;

  public ListService(ListRepository listRepository,
                     UserService userService,
                     RoomService roomService,
                     ListItemRepository listItemRepository) {
    this.listRepository = listRepository;
    this.userService = userService;
    this.roomService = roomService;
    this.listItemRepository = listItemRepository;
  }

  public ListResponse getListData(Long id, Authentication auth) throws EntityNotFoundException, AccessDeniedException {
    final List list =
      listRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("List with id: " + id + " does not exist!"));

    final Long userId = userService.getUserFromAuthentication(auth).getId();
    if (list.getUsers().stream().noneMatch(u -> u.getId().equals(userId))) {
      throw new AccessDeniedException("Requested list does not belong to you");
    }

    return new ListResponse(list);
  }

  public void createList(String title, Authentication auth) {
    final User user = userService.getUserFromAuthentication(auth);
    final List newList = new List(title, Set.of(user));
    listRepository.save(newList);
  }

  public void addItem(AddListItem req, Authentication auth) {
    final Long listId = req.getListId();
    final List list =
      listRepository.findById(listId).orElseThrow(() -> new ResourceNotFoundException("List with id: " + listId + " does not exist!"));

    final String itemContent = req.getContent();
    final ListItem newListItem = new ListItem(list, itemContent, req.getOrder());
    final ListItem savedItem = listItemRepository.save(newListItem);

    list.getItems().add(newListItem);
    listRepository.save(list);

    final Long userId = userService.getUserFromAuthentication(auth).getId();
    final String itemContentMessage = itemContent.length() > 20 ? itemContent.substring(0, 20) + "..." : itemContent;
    final String notificationMessage = "New Item "
                                       + "\"" + itemContentMessage + "\""
                                       + " in list " + list.getTitle();
    roomService.notifyRoom(listId, userId, new ItemCreationNotification(notificationMessage, savedItem));
  }
}
