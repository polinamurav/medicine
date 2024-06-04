package com.example.medicine.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityScan
@Table(name = "doctor_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddoctor;

    private String name;
    private String surname;
    private String specialization;
    private Date birth;
    private String phone;
    private String email;

    private LocalDateTime dateOfCreated;
    @OneToOne(mappedBy = "doctorEntity", cascade = CascadeType.ALL)
    private ImageDoctorEntity images;

    @OneToOne
    @JoinColumn(name = "iduser", referencedColumnName = "iduser")
    private UserEntity user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointmentsList;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<ServiceEntity> services; // отношение с услугами

    public void addImageToDoctor(ImageDoctorEntity images) {
        images.setDoctorEntity(this);
        this.images = images;
    }

}
