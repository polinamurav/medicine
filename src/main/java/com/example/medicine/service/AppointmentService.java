package com.example.medicine.service;

import com.example.medicine.Entity.Appointment;
import com.example.medicine.Entity.AppointmentStatus;
import com.example.medicine.Entity.DoctorEntity;
import com.example.medicine.repository.AppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepo appointmentRepo;

    private final DoctorService doctorService;

    public AppointmentService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }



    public Appointment getAppointmentById(Long id) {
        return appointmentRepo.findById(id).orElse(null);
    }


    public void saveAppointment(Appointment appointment) throws Exception {
        appointmentRepo.save(appointment);
    }



    public List<Object[]> getAppointmentStatusStatistics() {
        return appointmentRepo.getAppointmentStatusStatistics();
    }


    public List<Object[]> getServiceStatistics() {
        return appointmentRepo.getServiceStatistics();
    }

    public List<Object[]> getTimeStatistics() {
        return appointmentRepo.getAppointmentTimeStatistics();
    }


}
