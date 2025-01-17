package com.example.shopping_list.list;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ListConfig {
  private final UserRepository userRepository;
  private final ListRepository listRepository;

  @Bean
  CommandLineRunner listCommandLineRunner(ListRepository repository) {
    return args -> {
      User user = userRepository.findByEmail("mike.example@mail.com").orElseThrow(() -> new IllegalArgumentException("User not found"));
      List list1 = new List("List1", user);
      List list2 = new List("test title of a random list", user);
      listRepository.saveAll(java.util.List.of(list1, list2));
    };
  }
}
