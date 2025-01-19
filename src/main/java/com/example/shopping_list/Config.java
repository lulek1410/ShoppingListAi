package com.example.shopping_list;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.shopping_list.list.List;
import com.example.shopping_list.list.ListRepository;
import com.example.shopping_list.user.User;
import com.example.shopping_list.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Config {

  private final PasswordEncoder encoder;

  @Bean
  CommandLineRunner usrCommandLineRunner(UserRepository userRepository, ListRepository listRepository) {
    return args -> {
      User mike = new User("mike.example@mail.com", encoder.encode("simplePassphrase123%"), "Mike", "Jones");
      User angelika = new User("ann.mirco@mail.com", encoder.encode("abcd123#"), "Angelika", "Mirco");

      List list1 = new List("List1", Set.of(mike));
      List list2 = new List("test title of a random list", Set.of(mike, angelika));

      mike.setLists(Set.of(list1, list2));
      angelika.setLists(Set.of(list2));

      userRepository.saveAll(java.util.List.of(mike, angelika));
      listRepository.saveAll(java.util.List.of(list1, list2));
    };
  }
}
