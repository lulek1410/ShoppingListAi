package com.example.shopping_list.list;

import com.example.shopping_list.user.User;

import jakarta.persistence.Column;
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
@Table(name = "lists")
@Setter
@Getter
@RequiredArgsConstructor
public class List {
  @Id
  @SequenceGenerator(name = "list_sequence", sequenceName = "list_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_sequence")
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT") private String title;

  @ManyToOne @JoinColumn(name = "owner_id", nullable = false) private User owner;
}
