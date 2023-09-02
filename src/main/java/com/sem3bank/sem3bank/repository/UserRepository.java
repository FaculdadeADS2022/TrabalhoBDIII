package com.sem3bank.sem3bank.repository;

import com.sem3bank.sem3bank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from usuario where email =:email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "select * from usuario where username =:username", nativeQuery = true)
    User findByUsername(@Param("username") String username);

}
