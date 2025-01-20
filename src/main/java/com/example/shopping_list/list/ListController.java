package com.example.shopping_list.list;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shopping_list.dto.response.Response;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping(path = "api/list")
public class ListController {
  private final ListService listService;

  public ListController(ListService listService) { this.listService = listService; }

  @GetMapping(path = "/{id}")
  public ResponseEntity<Object> getList(@PathVariable Long id, Authentication auth) {
    try {
      return ResponseEntity.ok().body(listService.getListData(id, auth));
    } catch (EntityNotFoundException error) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(error.getMessage()));
    } catch (AccessDeniedException error) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(error.getMessage()));
    }
  }
}
