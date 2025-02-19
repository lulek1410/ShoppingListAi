package com.example.shopping_list.list_item;

import java.io.Serializable;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "list_items")
@Getter
@Setter
@NoArgsConstructor
public class ListItem implements Serializable {
  @Id
  @SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence", allocationSize = 1)
  @GeneratedValue(generator = "list_item_sequence", strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne @JoinColumn(name = "list_id", nullable = false) private List list;

  @Column(nullable = false, columnDefinition = "TEXT") private String content;

  @Column(nullable = false) @ColumnDefault("false") private boolean checked;

  @Column(nullable = false) private int itemOrder;

  public ListItem(List list, String content, int itemOrder) {
    this.list = list;
    this.content = content;
    this.itemOrder = itemOrder;
  }
}
