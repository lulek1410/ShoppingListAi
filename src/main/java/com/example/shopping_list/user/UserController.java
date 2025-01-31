package com.example.shopping_list.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping_list.dto.exception.ResourceNotFoundException;
import com.example.shopping_list.dto.response.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @DeleteMapping("/removeList/{listId}")
  public ResponseEntity<Object> removeList(@PathVariable Long listId, Authentication authentication) {
    try {
      userService.removeList(listId, authentication);
      return ResponseEntity.ok().build();
    } catch (AccessDeniedException error) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(error.getMessage()));
    } catch (ResourceNotFoundException error) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(error.getMessage()));
    } catch (Exception error) {
      log.error("UserController::removeList: Could not remove list from user relation: " + error.getMessage());
      return ResponseEntity.internalServerError().body(new Response("Could not remove list. Please try again later."));
    }
  }
}
