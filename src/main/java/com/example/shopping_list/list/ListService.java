package com.example.shopping_list.list;

import org.springframework.stereotype.Service;

@Service
public class ListService {
  private final ListRepository listRepository;

  public ListService(ListRepository listRepository) { this.listRepository = listRepository; }

  public java.util.List<ListResponse> getListsByUserId(Long id) {
    java.util.List<List> lists = listRepository.findByOwner(id);
    java.util.List<ListResponse> response = new java.util.ArrayList<>();
    for (List list : lists) {
      response.add(new ListResponse(list.getId(), list.getTitle()));
    }
    return response;
  }
}
