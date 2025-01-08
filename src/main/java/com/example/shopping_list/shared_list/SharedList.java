package com.example.shopping_list.shared_list;

import com.example.shopping_list.list.List;
import com.example.shopping_list.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shared_lists")
@Getter
@Setter
@RequiredArgsConstructor
public class SharedList {
  @Id
  @SequenceGenerator(name = "shared_list_sequence", sequenceName = "shared_list_sequence", allocationSize = 1)
  @GeneratedValue(generator = "shared_list_sequence", strategy = GenerationType.SEQUENCE)
  private final Long id;

  @ManyToOne @JoinColumn(name = "list_id", nullable = false) private final List listId;
  @ManyToOne @JoinColumn(name = "userId", nullable = false) private final User userId;
}
