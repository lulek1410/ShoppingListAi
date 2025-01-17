package com.example.shopping_list.list;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ListRepository extends JpaRepository<List, Long> {

  @Query("SELECT l FROM List l WHERE l.owner.id = :ownerId") java.util.List<List> findByOwner(@Param("ownerId") Long ownerId);
}
