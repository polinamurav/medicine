package com.example.medicine.service;

import com.example.medicine.Entity.ServiceEntity;
import com.example.medicine.repository.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepo serviceRepository;

    @Autowired
    public ServiceService (ServiceRepo serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

}
