package com.example.shopping_list.list;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.shopping_list.list_item.ListItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lists")
@Setter
@Getter
@NoArgsConstructor
public class List implements Serializable {
  @Id
  @SequenceGenerator(name = "list_sequence", sequenceName = "list_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_sequence")
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT") private String title;

  @OneToMany(mappedBy = "list", orphanRemoval = true, cascade = CascadeType.ALL) private Set<ListItem> items = new HashSet<>();

  public List(String title) { this.title = title; }
}
