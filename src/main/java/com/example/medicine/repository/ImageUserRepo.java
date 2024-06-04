package com.example.medicine.repository;

import com.example.medicine.Entity.ImageUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageUserRepo extends JpaRepository<ImageUserEntity, Long> {

//    void deleteByUserId(Long id);
}
