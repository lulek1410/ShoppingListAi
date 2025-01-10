package com.example.shopping_list.user;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UserConfig {

  private final PasswordEncoder encoder;

  @Bean
  CommandLineRunner commandLineRunner(UserRepository repository) {
    return args -> {
      User mike = new User("mike.example@mail.com", encoder.encode("simplePassphrase123%"), "Mike", "Jones");
      User angelika = new User("ann.mirco@mail.com", encoder.encode("abcd123#"), "Angelika", "Mirco");
      repository.saveAll(List.of(mike, angelika));
    };
  }
}
