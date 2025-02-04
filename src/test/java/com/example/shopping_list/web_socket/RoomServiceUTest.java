package com.example.shopping_list.web_socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.shopping_list.dto.response.notification.Notification;

@ExtendWith(MockitoExtension.class)
class RoomServiceUTest {

  private RoomService roomService;

  @Mock private WebSocketSession session1;
  @Mock private WebSocketSession session2;
  @Mock private WebSocketSession session3;

  private final Long listId = 1L;
  private final Long userId1 = 100L;
  private final Long userId2 = 101L;
  private final Long userId3 = 102L;

  @BeforeEach
  void setup() {
    roomService = new RoomService();
  }

  @Nested
  class AddUserTests {
    @Test
    void testAddUserToRoom() {
      roomService.addUserToRoom(listId, userId1, session1);

      assertTrue(roomService.getActiveRooms().contains(listId), "Room should be active after adding a user.");
    }
  }

  @Nested
  class RemoveUserTests {
    @Test
    void testRemoveUserFromRoom_RemovesRoomWhenEmpty() {
      roomService.addUserToRoom(listId, userId1, session1);
      roomService.removeUserFromRoom(listId, userId1);

      assertFalse(roomService.getActiveRooms().contains(listId), "Room should be removed when the last user leaves.");
    }

    @Test
    void testRemoveUserFromRoom_KeepsRoomWhenNotEmpty() {
      roomService.addUserToRoom(listId, userId1, session1);
      roomService.addUserToRoom(listId, userId2, session2);

      roomService.removeUserFromRoom(listId, userId1);

      assertTrue(roomService.getActiveRooms().contains(listId), "Room should remain active if users are still present.");
    }
  }

  @Nested
  class NotificationTests {
    @Test
    void testNotifyRoom_SendsMessageToOtherUsers() throws IOException {
      Notification notification = new Notification("Test Message");

      roomService.addUserToRoom(listId, userId1, session1);
      roomService.addUserToRoom(listId, userId2, session2);
      roomService.addUserToRoom(listId, userId3, session3);

      doNothing().when(session2).sendMessage(any(TextMessage.class));
      doNothing().when(session3).sendMessage(any(TextMessage.class));

      roomService.notifyRoom(listId, userId1, notification);

      verify(session2, times(1)).sendMessage(any(TextMessage.class));
      verify(session3, times(1)).sendMessage(any(TextMessage.class));
      verify(session1, never()).sendMessage(any(TextMessage.class));
    }
  }

  @Nested
  class ActiveRoomsTests {
    @Test
    void testGetActiveRooms() {
      roomService.addUserToRoom(listId, userId1, session1);
      roomService.addUserToRoom(2L, userId2, session2);

      Set<Long> activeRooms = roomService.getActiveRooms();

      assertEquals(2, activeRooms.size(), "There should be two active rooms.");
      assertTrue(activeRooms.contains(listId));
      assertTrue(activeRooms.contains(2L));
    }
  }
}
