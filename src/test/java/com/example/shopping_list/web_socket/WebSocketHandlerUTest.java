package com.example.shopping_list.web_socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.example.shopping_list.security.JWTService;
import com.example.shopping_list.user_list.UserListRepository;

@ExtendWith(MockitoExtension.class)
class WebSocketHandlerUTest {

  @Mock private UserListRepository userListRepository;
  @Mock private RoomService roomService;
  @Mock private JWTService jwtService;
  @Mock private WebSocketSession session;

  @InjectMocks private WebSocketHandler webSocketHandler;

  private final Long userId = 100L;
  private final Long listId = 1L;

  @BeforeEach
  void setup() {
    lenient().when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
  }

  @Nested
  class AfterConnectionEstablishedTests {
    @Test
    void testUserJoinsRoomsOnConnection() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer validToken");

      when(session.getHandshakeHeaders()).thenReturn(headers);
      when(jwtService.generateUserIdFromToken("validToken")).thenReturn(userId);
      when(userListRepository.getListIdsByUserId(userId)).thenReturn(Set.of(listId));

      webSocketHandler.afterConnectionEstablished(session);

      verify(roomService, times(1)).addUserToRoom(listId, userId, session);
    }

    @Test
    void testNoRoomsJoinedIfUserHasNoLists() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer validToken");

      when(session.getHandshakeHeaders()).thenReturn(headers);
      when(jwtService.generateUserIdFromToken("validToken")).thenReturn(userId);
      when(userListRepository.getListIdsByUserId(userId)).thenReturn(Collections.emptySet());

      webSocketHandler.afterConnectionEstablished(session);

      verify(roomService, never()).addUserToRoom(anyLong(), anyLong(), any());
    }

    @Test
    void testInvalidTokenThrowsExceptionAndUserDoesNotJoinRooms() {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer invalidToken");

      when(session.getHandshakeHeaders()).thenReturn(headers);
      when(jwtService.generateUserIdFromToken("invalidToken")).thenReturn(null);

      assertThrows(BadRequestException.class, () -> webSocketHandler.afterConnectionEstablished(session));

      verify(roomService, never()).addUserToRoom(anyLong(), anyLong(), any());
    }

    @Test
    void testMissingAuthorizationHeaderThrowsException() {
      assertThrows(BadRequestException.class, () -> webSocketHandler.afterConnectionEstablished(session));
    }
  }

  @Nested
  class AfterConnectionClosedTests {
    @Test
    void testUserLeavesRoomsOnDisconnection() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer validToken");

      when(session.getHandshakeHeaders()).thenReturn(headers);
      when(jwtService.generateUserIdFromToken("validToken")).thenReturn(userId);
      when(roomService.getActiveRooms()).thenReturn(Set.of(listId));

      webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

      verify(roomService, times(1)).removeUserFromRoom(listId, userId);
    }

    @Test
    void testNoRoomsLeftIfNoActiveRooms() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer validToken");

      when(session.getHandshakeHeaders()).thenReturn(headers);
      when(jwtService.generateUserIdFromToken("validToken")).thenReturn(userId);
      when(roomService.getActiveRooms()).thenReturn(Collections.emptySet());

      webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

      verify(roomService, never()).removeUserFromRoom(anyLong(), anyLong());
    }

    @Test
    void testInvalidTokenThrowsExceptionAndUserDoesNotLeaveRooms() {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer invalidToken");

      when(session.getHandshakeHeaders()).thenReturn(headers);
      when(jwtService.generateUserIdFromToken("invalidToken")).thenReturn(null);

      assertThrows(BadRequestException.class, () -> webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL));

      verify(roomService, never()).removeUserFromRoom(anyLong(), anyLong());
    }
  }
}
