package com.example.shopping_list.web_socket;

import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.shopping_list.security.JWTService;
import com.example.shopping_list.user_list.UserListRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

  private final UserListRepository userListItemRepository;
  private final RoomService roomService;
  private final JWTService jwtService;

  public WebSocketHandler(UserListRepository userListItemRepository, RoomService roomService, JWTService jwtService) {
    this.userListItemRepository = userListItemRepository;
    this.roomService = roomService;
    this.jwtService = jwtService;
  }

  private Long getUserIdFromSession(@NonNull WebSocketSession session) throws BadRequestException {
    String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
    if (authHeader == null) {
      log.error("WebSocketHandler::getUserIdFromSession: Authorization header not provided");
      throw new BadRequestException("WebSocketHandler::getUserIdFromSession: Authorization header not provided");
    }
    if (!authHeader.startsWith("Bearer ")) {
      log.error("WebSocketHandler::getUserIdFromSession: Authorization header invalid");
      throw new BadRequestException("WebSocketHandler::getUserIdFromSession: Authorization header is not valid");
    }
    String token = authHeader.substring(7);
    Long userId = jwtService.generateUserIdFromToken(token);
    if (userId == null) {
      throw new BadRequestException("WebSocketHandler::getUserIdFromSession: Token invalid");
    }
    return userId;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    final Long userId = getUserIdFromSession(session);
    if (userId == null) {
      return;
    }
    Set<Long> userListsIds = userListItemRepository.getListIdsByUserId(userId);
    for (Long listId : userListsIds) {
      roomService.addUserToRoom(listId, userId, session);
    }
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
    Long userId = getUserIdFromSession(session);
    Set<Long> rooms = roomService.getActiveRooms();
    for (Long listId : rooms) {
      roomService.removeUserFromRoom(listId, userId);
    }
  }
}
