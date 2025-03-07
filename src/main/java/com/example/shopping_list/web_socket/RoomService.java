package com.example.shopping_list.web_socket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.shopping_list.dto.response.notification.Notification;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomService {

  private final Map<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
  private final Map<Long, WebSocketSession> usersSessions = new ConcurrentHashMap<>();

  public void addUserToRoom(Long listId, Long userId, WebSocketSession session) {
    rooms.computeIfAbsent(listId, key -> ConcurrentHashMap.newKeySet()).add(session);
    usersSessions.put(userId, session);
  }

  public void removeUserFromRoom(Long listId, Long userId) {
    Set<WebSocketSession> room = rooms.get(listId);
    WebSocketSession session = usersSessions.get(userId);
    if (room != null) {
      room.remove(session);
      if (room.isEmpty()) {
        rooms.remove(listId);
      }
    } else {
      log.error("RoomService::removeUserFromRoom: room with id: " + listId + " does not exist");
    }
  }

  public void notifyRoom(Long listId, Long triggeringUserId, Notification notificationContent) {
    Set<WebSocketSession> room = rooms.get(listId);
    if (room == null) {
      log.error("RoomService::notifyRoom: room with id: " + listId + " does not exist");
      return;
    }
    try {
      String messageJson = new ObjectMapper().writeValueAsString(notificationContent);
      TextMessage notification = new TextMessage(messageJson);

      WebSocketSession triggeringSession = usersSessions.get(triggeringUserId);
      room.stream().filter(session -> !session.equals(triggeringSession)).forEach(session -> {
        try {
          session.sendMessage(notification);
        } catch (IOException e) {
          log.error("Error sending WebSocket message", e);
        }
      });
    } catch (Exception e) {
      log.error("Error serializing message object", e);
    }
  }

  public Set<Long> getActiveRooms() { return rooms.keySet(); }
}