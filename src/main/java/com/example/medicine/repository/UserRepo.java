package com.example.medicine.repository;

import com.example.medicine.Entity.Role;
import com.example.medicine.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRolesAndNameStartingWithIgnoreCase(Role role, String keyword);
    List<UserEntity> findByRolesAndSurnameStartingWithIgnoreCase(Role role, String keyword);
}
