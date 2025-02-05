package com.example.shopping_list.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class UserRepositoryITest {

  @Autowired private TestEntityManager entityManager;
  @Autowired private UserRepository userRepository;

  @Test
  void testFindByEmail_ExistingUser() {
    final String email = "mike.wazowski@mail.com";
    final User user = new User(email, "mw123#", "Mike", "Wazowski");
    entityManager.persist(user);
    entityManager.flush();

    Optional<User> foundUser = assertDoesNotThrow(() -> userRepository.findByEmail(email));

    assertTrue(foundUser.isPresent());
    assertEquals(user, foundUser.get());
  }

  @Test
  void testFindByEmail_NonExistentUser() {
    final String email = "mike.wazowski@mail.com";

    Optional<User> foundUser = assertDoesNotThrow(() -> userRepository.findByEmail(email));

    assertFalse(foundUser.isPresent());
  }
}
