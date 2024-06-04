package com.example.medicine.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

@Entity
@EntityScan
@Table(name = "image_doctor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image_doctor")
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "bytes", columnDefinition = "blob")
    private byte[] bytes;

    @OneToOne
    @JoinColumn(name = "iddoctor")
    private DoctorEntity doctorEntity;

}
