package com.example.shopping_list.list;

import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ListService {
  private final ListRepository listRepository;
  private final UserService userService;

  public ListService(ListRepository listRepository, UserService userService) {
    this.listRepository = listRepository;
    this.userService = userService;
  }

  public ListResponse getListData(Long id, Authentication auth) throws EntityNotFoundException, AccessDeniedException {
    final List list =
      listRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("List with id: " + id + " does not exist!"));

    final Long userId = userService.getUserFromAuthentication(auth).getId();
    if (list.getUsers().stream().noneMatch(u -> u.getId().equals(userId))) {
      throw new AccessDeniedException("Requested list does not belong to you");
    }

    return new ListResponse(list);
  }

  public void createList(String title, Authentication auth) {
    User user = userService.getUserFromAuthentication(auth);
    List newList = new List(title, Set.of(user));
    listRepository.save(newList);
  }
}
