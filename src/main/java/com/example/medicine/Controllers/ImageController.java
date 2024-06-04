package com.example.medicine.Controllers;

import com.example.medicine.Entity.ImageUserEntity;
import com.example.medicine.repository.ImageDoctorRepo;
import com.example.medicine.repository.ImageUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageUserRepository imageUserRepository;
    private final Path imageLocation = Paths.get("src/main/resources/static/images");

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<ImageUserEntity> imageEntityOptional = imageUserRepository.findById(id);

        if (imageEntityOptional.isPresent()) {
            ImageUserEntity imageEntity = imageEntityOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "image/jpeg"); // Или другой соответствующий тип контента
            headers.set("Content-Disposition", "inline; filename=\"" + imageEntity.getName() + "\"");
            return new ResponseEntity<>(imageEntity.getBytes(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
