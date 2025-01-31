package com.example.shopping_list.user_list;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.shopping_list.list.List;

@Repository
public interface UserListRepository extends JpaRepository<UserList, UserListId> {

  @Query("SELECT ul.user.id FROM UserList ul WHERE ul.list.id = :listId") Set<Long> getUserIdsByListId(Long listId);
  @Query("SELECT ul.list.id FROM UserList ul WHERE ul.user.id = :userId") Set<Long> getListIdsByUserId(Long userId);

  @Query("SELECT ul.list FROM UserList ul WHERE ul.user.id = :userId") Set<List> getListsByUserId(Long userId);
}
