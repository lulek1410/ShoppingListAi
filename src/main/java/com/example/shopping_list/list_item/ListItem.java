package com.example.shopping_list.list_item;

import org.hibernate.annotations.ColumnDefault;

import com.example.shopping_list.list.List;

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
@Table(name = "list_items")
@Getter
@Setter
@RequiredArgsConstructor
public class ListItem {
  @Id
  @SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence", allocationSize = 1)
  @GeneratedValue(generator = "list_item_sequence", strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne @JoinColumn(name = "list_id", nullable = false) private List list;

  @Column(nullable = false, columnDefinition = "TEXT") private String content;

  @Column(nullable = false) @ColumnDefault("false") private boolean checked;

  @Column(nullable = false) private int itemOrder;
}
