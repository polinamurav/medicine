package com.example.medicine.repository;

import com.example.medicine.Entity.DoctorEntity;
import com.example.medicine.Entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepo extends JpaRepository<ServiceEntity, Long> {
    // Метод для поиска услуг по доктору
    List<ServiceEntity> findByDoctor(DoctorEntity doctor);
}
