package com.example.shopping_list;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.shopping_list.list.List;
import com.example.shopping_list.list.ListRepository;
import com.example.shopping_list.list_item.ListItem;
import com.example.shopping_list.list_item.ListItemRepository;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Config {

  private final PasswordEncoder encoder;

  @Bean
  CommandLineRunner commandLineRunner(UserRepository userRepository, ListRepository listRepository, ListItemRepository listItemRepository) {
    return args -> {
      User mike = new User("mike.example@mail.com", encoder.encode("simplePassphrase123%"), "Mike", "Jones");
      User angelika = new User("ann.mirco@mail.com", encoder.encode("abcd123#"), "Angelika", "Mirco");

      List list1 = new List("Fruits", Set.of(mike));
      List list2 = new List("Vegetables", Set.of(mike, angelika));
      List list3 = new List("Empty", Set.of(angelika));

      mike.setLists(Set.of(list1, list2));
      angelika.setLists(Set.of(list2, list3));

      ListItem apple = new ListItem(list1, "apple x6", 1);
      ListItem orange = new ListItem(list1, "orange x4", 2);
      ListItem banana = new ListItem(list1, "banana", 3);
      ListItem potato = new ListItem(list2, "potato 500g", 1);

      list1.setItems(Set.of(apple, orange, banana));
      list2.setItems(Set.of(potato));

      userRepository.saveAll(java.util.List.of(mike, angelika));
      listRepository.saveAll(java.util.List.of(list1, list2, list3));
      listItemRepository.saveAll(java.util.List.of(apple, orange, banana, potato));
    };
  }
}
