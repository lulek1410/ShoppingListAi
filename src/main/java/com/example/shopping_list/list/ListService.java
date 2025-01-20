package com.example.shopping_list.list;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.shopping_list.user.User;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ListService {
  private final ListRepository listRepository;

  public ListService(ListRepository listRepository) { this.listRepository = listRepository; }

  public ListResponse getListData(Long id, Authentication auth) throws EntityNotFoundException, AccessDeniedException {
    final List list =
      listRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("List with id: " + id + " does not exist!"));

    final User user = (User)auth.getPrincipal();
    if (list.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
      throw new AccessDeniedException("Requested list does not belong to you");
    }

    return new ListResponse(list);
  }
}
