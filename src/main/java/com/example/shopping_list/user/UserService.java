package com.example.shopping_list.user;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping_list.list.List;
import com.example.shopping_list.list.ListRepository;
import com.example.shopping_list.web_socket.RoomService;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final ListRepository listRepository;
  private final RoomService roomService;

  public UserService(UserRepository userRepository, ListRepository listRepository, RoomService roomService) {
    this.userRepository = userRepository;
    this.listRepository = listRepository;
    this.roomService = roomService;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }
    return user.get();
  }

  public User getUserById(Long id) { return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found")); }

  public Set<Long> getUserListIds(Long userId) {
    Set<List> lists = userRepository.getUserListsIds(userId);
    return lists.stream().map(List::getId).collect(Collectors.toSet());
  }

  public User getUserFromAuthentication(Authentication authentication) {
    return authentication.getPrincipal() instanceof User user ? user : null;
  }

  @Transactional
  public void removeList(Long listId, Authentication authentication) throws NoSuchElementException, AccessDeniedException {
    User user = this.getUserFromAuthentication(authentication);
    Long userId = user.getId();
    List list = listRepository.findById(listId).orElseThrow(() -> new NoSuchElementException("List not found"));
    if (list.getUsers().stream().noneMatch((listUser -> listUser.getId().equals(userId)))) {
      throw new AccessDeniedException("Can't remove list. List does not belong to you");
    }
    if (list.getUsers().size() == 1) {
      listRepository.delete(list);
      return;
    }
    list.setUsers(list.getUsers().stream().filter(u -> !u.getId().equals(userId)).collect(Collectors.toSet()));
    listRepository.save(list);
    roomService.removeUserFromRoom(listId, userId);
    roomService.notifyRoom(
      listId, userId, "User \"" + user.getName() + " " + user.getSurname() + "\" has left the list \"" + list.getTitle() + "\"");
  }
}
