package com.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	    Optional<User> findByUsernameIgnoreCase(String username);

	    boolean existsByUsername(String username);

	    boolean existsByEmail(String email);
}
