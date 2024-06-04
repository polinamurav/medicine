package com.example.medicine.Controllers;

import com.example.medicine.Entity.ImageDoctorEntity;
import com.example.medicine.repository.ImageDoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/doctor-images")
public class DoctorImageController {

    @Autowired
    private ImageDoctorRepo imageDoctorRepo;
    private final Path imageLocation = Paths.get("src/main/resources/static/images");

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getDoctorImage(@PathVariable Long id) {
        Optional<ImageDoctorEntity> imageEntityOptional = imageDoctorRepo.findById(id);

        if (imageEntityOptional.isPresent()) {
            ImageDoctorEntity imageEntity = imageEntityOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "image/jpeg"); // Или другой соответствующий тип контента
            headers.set("Content-Disposition", "inline; filename=\"" + imageEntity.getName() + "\"");
            return new ResponseEntity<>(imageEntity.getBytes(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
