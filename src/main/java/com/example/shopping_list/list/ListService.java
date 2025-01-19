package com.example.shopping_list.list;

import org.springframework.stereotype.Service;

@Service
public class ListService {
  private final ListRepository listRepository;

  public ListService(ListRepository listRepository) { this.listRepository = listRepository; }
}
