package org.scrollSystem.repository;

import org.scrollSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u FROM User u " +
            "WHERE (:email is null OR u.email like CONCAT('%', :email, '%')) " +
            "AND (:username is null OR u.username  like CONCAT('%', :username, '%'))")
    List<User> getListUser(String email, String username);
}