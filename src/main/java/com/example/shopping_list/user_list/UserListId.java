package com.example.shopping_list.user_list;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserListId implements Serializable {
  private Long userId;
  private Long listId;

  public UserListId(Long userId, Long listId) {
    this.userId = userId;
    this.listId = listId;
  }
}
