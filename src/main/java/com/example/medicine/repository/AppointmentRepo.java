package com.example.medicine.repository;

import com.example.medicine.Entity.Appointment;
import com.example.medicine.Entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndAppointmentDateBetween(DoctorEntity doctor, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctorIddoctor(Long doctorId);

    List<Appointment> findByPatientIduser(Long patientId);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);


    @Query(value = "SELECT status, COUNT(*) AS count FROM Appointment GROUP BY status", nativeQuery = true)
    List<Object[]> getAppointmentStatusStatistics();

    @Query(value = "SELECT s.name, COUNT(*) AS count FROM appointment a JOIN service_entity s ON a.service_id = s.idservice GROUP BY s.name", nativeQuery = true)
    List<Object[]> getServiceStatistics();

    @Query(value = "SELECT HOUR(appointment_date) AS hour, COUNT(*) AS count FROM Appointment GROUP BY HOUR(appointment_date)", nativeQuery = true)
    List<Object[]> getAppointmentTimeStatistics();

}
