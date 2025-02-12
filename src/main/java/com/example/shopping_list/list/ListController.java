package com.example.shopping_list.list;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shopping_list.dto.exception.ResourceNotFoundException;
import com.example.shopping_list.dto.request.AddListItemRequest;
import com.example.shopping_list.dto.request.CreateListRequest;
import com.example.shopping_list.dto.response.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(path = "api/list")
@RequiredArgsConstructor
public class ListController {
  private final ListService listService;

  @GetMapping(path = "/{id}")
  public ResponseEntity<Object> getList(@PathVariable Long id, Authentication auth) {
    try {
      return ResponseEntity.ok().body(listService.getListData(id, auth));
    } catch (ResourceNotFoundException error) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(error.getMessage()));
    } catch (AccessDeniedException error) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(error.getMessage()));
    } catch (Exception error) {
      log.error("ListController::getList: List could not be queried " + error.getMessage());
      return ResponseEntity.internalServerError().body(new Response("Could not get list. Please try again later."));
    }
  }

  @PostMapping("/create")
  public ResponseEntity<Object> createList(@RequestBody CreateListRequest req, Authentication auth) {
    try {
      return ResponseEntity.created(URI.create("api/list/create")).body(listService.createList(req, auth));
    } catch (Exception error) {
      log.error("ListController::createList: List could not be created " + error.getMessage());
      return ResponseEntity.internalServerError().body(new Response("List could not be created. Try again later."));
    }
  }

  @PostMapping("/addItem")
  public ResponseEntity<Object> addItem(@RequestBody AddListItemRequest req, Authentication auth) {
    try {
      return ResponseEntity.created(URI.create("api/list/addItem")).body(listService.addItem(req, auth));
    } catch (ResourceNotFoundException error) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(error.getMessage()));
    } catch (Exception error) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new Response("Something went wrong when adding an item. Please try again later."));
    }
  }
}
