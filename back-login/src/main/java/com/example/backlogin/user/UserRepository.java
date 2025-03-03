package com.example.backlogin.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.mail= :mail")
    Optional<User> getUserByMail(@Param("mail") String mail);

    @Query("SELECT u FROM User u WHERE u.username= :username")
    Optional<User> getUserByUsername(@Param("username") String username);
}
