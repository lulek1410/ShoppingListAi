package com.example.shopping_list.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.shopping_list.list.List;
import com.example.shopping_list.user_list.UserList;
import com.example.shopping_list.user_list.UserListRepository;

@DataJpaTest
class UserListRepositoryITest {

  @Autowired private UserListRepository userListRepository;
  @Autowired private TestEntityManager entityManager;

  private <T> T persist(T entity) {
    entityManager.persist(entity);
    entityManager.flush();
    return entity;
  }

  @Test
  void testGetUserIdsByListId() {
    User user1 = persist(new User("j.doe@mail.com", "jd389.doo#", "John", "Doe"));
    List list = persist(new List("Grocery List"));

    persist(new UserList(user1, list, 1));

    assertEquals(Set.of(user1.getId()), userListRepository.getUserIdsByListId(list.getId()));
  }

  @Test
  void testGetListIdsByUserId() {
    User user1 = persist(new User("j.doe@mail.com", "jd389.doo#", "John", "Doe"));
    User user2 = persist(new User("jane.doe@mail.com", "jo123.ned#", "Jane", "Doe"));
    List list1 = persist(new List("Grocery List"));
    List list2 = persist(new List("Films list"));

    persist(new UserList(user1, list1, 1));
    persist(new UserList(user1, list2, 2));

    assertEquals(Set.of(list1.getId(), list2.getId()), userListRepository.getListIdsByUserId(user1.getId()));
    assertTrue(userListRepository.getListIdsByUserId(user2.getId()).isEmpty());
  }

  @Test
  void testGetListsByUserId() {
    User user1 = persist(new User("j.doe@mail.com", "jd389.doo#", "John", "Doe"));
    User user2 = persist(new User("jane.doe@mail.com", "jo123.ned#", "Jane", "Doe"));
    List list1 = persist(new List("Grocery List"));
    List list2 = persist(new List("Films list"));

    persist(new UserList(user1, list1, 1));
    persist(new UserList(user1, list2, 2));

    assertEquals(Set.of(list1, list2), userListRepository.getListsByUserId(user1.getId()));
    assertTrue(userListRepository.getListsByUserId(user2.getId()).isEmpty());
  }
}
