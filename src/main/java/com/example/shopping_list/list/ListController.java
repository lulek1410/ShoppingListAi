package com.example.shopping_list.list;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "api/list")
public class ListController {
  private final ListService listService;

  public ListController(ListService listService) { this.listService = listService; }
}
