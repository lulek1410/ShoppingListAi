package com.example.shopping_list.dto.response.notification;

import lombok.Data;

@Data
public class Notification {
  private final String message;
  private final NotificationType type;

  public Notification(String message) {
    this.type = NotificationType.MESSAGE;
    this.message = message;
  }

  protected Notification(String message, NotificationType type) {
    this.type = type;
    this.message = message;
  }
}
