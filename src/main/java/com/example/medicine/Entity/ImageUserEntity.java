package com.example.medicine.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

@Entity
@EntityScan
@Table(name = "image_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image_user")
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "bytes", columnDefinition = "blob", length = 10485760)
    private byte[] bytes;


    @OneToOne
    @JoinColumn(name = "iduser")
    private UserEntity userEntity;

}
