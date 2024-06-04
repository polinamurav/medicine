package com.example.medicine.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data //чтобы не писать конструктор геттер, сеттер - lombok
@AllArgsConstructor //конструктор со всеми полями, которые присутствуют
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idappointment;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime appointmentDate;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    private String comment;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "iduser")
    private UserEntity patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "iddoctor")
    private DoctorEntity doctor;

}

