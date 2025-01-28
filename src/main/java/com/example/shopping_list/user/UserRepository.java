package com.example.shopping_list.user;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.shopping_list.list.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  @Query("SELECT l FROM List l JOIN l.users u WHERE u.id = :userId") Set<List> getUserListsIds(Long userId);
}
