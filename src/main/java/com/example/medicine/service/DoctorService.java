package com.example.medicine.service;

import com.example.medicine.Entity.*;
import com.example.medicine.repository.DoctorRepo;
import com.example.medicine.repository.ImageDoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private final DoctorRepo doctorRepo;

    private final ImageDoctorRepo imageDoctorRepo;

    public DoctorService(DoctorRepo doctorRepo, ImageDoctorRepo imageDoctorRepo) {
        this.doctorRepo = doctorRepo;
        this.imageDoctorRepo = imageDoctorRepo;
    }

    public List<DoctorEntity> getAllDoctors() {
        return (List<DoctorEntity>) doctorRepo.findAll();
    }

    public DoctorEntity getDoctorById(Long doctorId) {
        Optional<DoctorEntity> doctor = doctorRepo.findById(doctorId);
        if (doctor.isPresent()) {
            return doctor.get();
        } else {
            throw new RuntimeException("Doctor not found with id " + doctorId);
        }
    }


    public List<DoctorEntity> getDoctorsByServiceId(Long serviceId) {
        // Получаем список всех врачей
        List<DoctorEntity> allDoctors = doctorRepo.findAll();

        // Создаем список для хранения врачей, предоставляющих выбранную услугу
        List<DoctorEntity> doctorsByService = new ArrayList<>();

        // Проходим по каждому врачу и проверяем, предоставляет ли он выбранную услугу
        for (DoctorEntity doctor : allDoctors) {
            // Получаем список услуг, предоставляемых текущим врачом
            List<ServiceEntity> services = doctor.getServices();

            // Проверяем, содержится ли выбранная услуга в списке услуг текущего врача
            for (ServiceEntity service : services) {
                if (service.getIdservice().equals(serviceId)) {
                    // Если услуга найдена, добавляем врача в список
                    doctorsByService.add(doctor);
                    break; // Прерываем внутренний цикл, так как услуга уже найдена для текущего врача
                }
            }
        }

        return doctorsByService;
    }



    private ImageDoctorEntity toImageEntity(MultipartFile file1) throws IOException {
        ImageDoctorEntity images = new ImageDoctorEntity();
        images.setName(file1.getOriginalFilename());
        images.setBytes(file1.getBytes());
        return images;
    }

    @Transactional
    public void updateDoctor(DoctorEntity doctorEntity, Long imageId, MultipartFile imageFile) throws IOException {
        // Проверяем, что файл изображения передан
        if (imageFile != null && !imageFile.isEmpty()) {

            // Проверяем, существует ли изображение с заданным id
            Optional<ImageDoctorEntity> existingImageOptional = imageDoctorRepo.findById(imageId);

            // Если изображение существует, обновляем его
            if (existingImageOptional.isPresent()) {
                ImageDoctorEntity existingImage = existingImageOptional.get();
                updateImage(existingImage, imageFile);
            } else {
                // Если изображение с указанным id не существует, сохраняем новое изображение для пользователя
                ImageDoctorEntity newImage = toImageEntity(imageFile); // Преобразуем MultipartFile в сущность ImageUserEntity
                imageDoctorRepo.save(newImage); // Сохраняем новое изображение в базе данных
                doctorEntity.addImageToDoctor(newImage); // Связываем изображение с пользователем
            }
        }

        // Получаем существующего пользователя из базы данных
        Optional<DoctorEntity> existingUserOptional = doctorRepo.findById(doctorEntity.getIddoctor());
        if (existingUserOptional.isPresent()) {
            DoctorEntity existingUser = existingUserOptional.get();
            // Обновляем поля пользователя
            existingUser.setName(doctorEntity.getName());
            existingUser.setSurname(doctorEntity.getSurname());
            existingUser.setBirth(doctorEntity.getBirth());
            existingUser.setServices(doctorEntity.getServices());
            existingUser.setPhone(doctorEntity.getPhone());
            existingUser.setEmail(doctorEntity.getEmail());

            // Если новое изображение было добавлено или обновлено, устанавливаем его для пользователя
            if (imageFile != null && !imageFile.isEmpty()) {
                Optional<ImageDoctorEntity> updatedImageOptional = imageDoctorRepo.findById(imageId);
                updatedImageOptional.ifPresent(existingUser::addImageToDoctor); // Устанавливаем обновленное изображение для пользователя
            }

            // Сохраняем обновленного пользователя
            doctorRepo.save(existingUser);
        } else {
            // Если пользователя не найдено в базе данных, можно сгенерировать исключение или выполнить другие действия в зависимости от требований проекта
            throw new EntityNotFoundException("Doctor with id " + doctorEntity.getIddoctor() + " not found");
        }
    }


    private void updateImage(ImageDoctorEntity image, MultipartFile newImageFile) throws IOException {
        image.setBytes(newImageFile.getBytes()); // Обновляем содержимое изображения

        image.setName(newImageFile.getOriginalFilename());
    }


}
