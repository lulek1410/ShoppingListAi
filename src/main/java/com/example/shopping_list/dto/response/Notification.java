package com.example.shopping_list.dto.response;

import lombok.Data;

@Data
public class Notification {
  private final String message;
  private final NotificationType type;
}
