package com.example.shopping_list.user;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

  @Bean
  CommandLineRunner commandLineRunner(UserRepository repository) {
    return args -> {
      User mike = new User("Mike123", "simplePassphrase123%", "mike.example@mail.com");
      User angie = new User("anmir", "abcd123#", "ann.mirco@mail.com");
      repository.saveAll(List.of(mike, angie));
    };
  }
}
