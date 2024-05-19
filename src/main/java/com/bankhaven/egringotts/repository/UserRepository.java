package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserById(String id);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :userInfo OR u.phoneNumber = :userInfo OR u.firstName = :userInfo OR u.lastName = :userInfo")
    List<User> findByEmailOrPhoneNumberOrName(@Param("userInfo") String email, @Param("userInfo") String phoneNumber, @Param("userInfo") String name);
}
