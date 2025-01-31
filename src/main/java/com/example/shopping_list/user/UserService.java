package com.example.shopping_list.user;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping_list.dto.exception.ResourceNotFoundException;
import com.example.shopping_list.dto.response.notification.Notification;
import com.example.shopping_list.list.List;
import com.example.shopping_list.list.ListRepository;
import com.example.shopping_list.user_list.UserListId;
import com.example.shopping_list.user_list.UserListRepository;
import com.example.shopping_list.utils.UserUtils;
import com.example.shopping_list.web_socket.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserListRepository userListRepository;
  private final ListRepository listRepository;
  private final RoomService roomService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }
    return user.get();
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Transactional
  public void removeList(Long listId, Authentication authentication) throws ResourceNotFoundException, AccessDeniedException {
    User user = UserUtils.getUserFromAuthentication(authentication);
    Long userId = user.getId();
    List list = listRepository.findById(listId).orElseThrow(() -> new ResourceNotFoundException("List not found"));

    Set<Long> listUsersIds = userListRepository.getUserIdsByListId(listId);
    if (!listUsersIds.contains(userId)) {
      throw new AccessDeniedException("Can't remove list. List does not belong to you");
    }

    userListRepository.deleteById(new UserListId(userId, listId));
    if (listUsersIds.size() == 1) {
      listRepository.delete(list);
    }

    roomService.removeUserFromRoom(listId, userId);
    roomService.notifyRoom(
      listId,
      userId,
      new Notification("User \"" + user.getName() + " " + user.getSurname() + "\" has left the list \"" + list.getTitle() + "\""));
  }
}
