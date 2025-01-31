package com.example.shopping_list.user_list;

import com.example.shopping_list.list.List;
import com.example.shopping_list.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users_lists_items")
@Setter
@Getter
@NoArgsConstructor
public class UserList {

  @EmbeddedId private UserListId id;

  @ManyToOne @MapsId("listId") @JoinColumn(name = "list_id", nullable = false) private List list;
  @ManyToOne @MapsId("userId") @JoinColumn(name = "user_id", nullable = false) private User user;
  @Column(nullable = false) private int listOrder;

  public UserList(User user, List list, int listOrder) {
    this.id = new UserListId(user.getId(), list.getId());
    this.user = user;
    this.list = list;
    this.listOrder = listOrder;
  }
}
