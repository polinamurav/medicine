package com.example.medicine.repository;

import com.example.medicine.Entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DoctorRepo extends JpaRepository<DoctorEntity, Long> {

}
