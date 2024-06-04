package com.example.medicine.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

@Entity
@EntityScan
@Table(name = "service_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idservice;

    private String name;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorEntity doctor; // Добавлено поле для связи с врачом

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<Appointment> appointments; // Добавлено поле для связи с записями о приемах


}
