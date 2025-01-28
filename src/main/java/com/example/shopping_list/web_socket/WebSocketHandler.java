package com.example.shopping_list.web_socket;

import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.shopping_list.security.JWTService;
import com.example.shopping_list.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

  private final UserService userService;
  private final RoomService roomService;
  private final JWTService jwtService;

  public WebSocketHandler(UserService userService, RoomService roomService, JWTService jwtService) {
    this.userService = userService;
    this.roomService = roomService;
    this.jwtService = jwtService;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
    if (authHeader == null) {
      log.error("WebSocketHandler::afterConnectionEstablished: Authorization header not provided");
      return;
    }
    if (!authHeader.startsWith("Bearer ")) {
      log.error("WebSocketHandler::afterConnectionEstablished: Authorization header invalid");
      return;
    }
    String token = authHeader.substring(7);
    Long userId = jwtService.generateUserIdFromToken(token);
    if (userId == null) {
      return;
    }
    log.info(userId.toString());
    Set<Long> userListIds = userService.getUserListIds(userId);
    for (Long listId : userListIds) {
      roomService.addUserToRoom(listId, session);
    }
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
    Set<Long> rooms = roomService.getActiveRooms();
    for (Long listId : rooms) {
      roomService.removeUserFromRoom(listId, session);
    }
  }
}
