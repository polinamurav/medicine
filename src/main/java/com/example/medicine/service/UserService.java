package com.example.medicine.service;

import com.example.medicine.Entity.DoctorEntity;
import com.example.medicine.Entity.ImageUserEntity;
import com.example.medicine.Entity.Role;
import com.example.medicine.Entity.UserEntity;
import com.example.medicine.repository.DoctorRepo;
import com.example.medicine.repository.ImageUserRepo;
import com.example.medicine.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepository;
    private final DoctorRepo doctorRepo;
    private final ImageUserRepo imageUserRepo;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(UserEntity user, boolean isDoctor) {
        String username = user.getUsername();
        if (userRepository.findByUsername(username) != null) {
            return false; // Такой пользователь уже существует
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифрование пароля
        if (isDoctor) {
            user.getRoles().add(Role.ROLE_DOCTOR);
        } else {
            user.getRoles().add(Role.ROLE_USER);
        }
        log.info("Saving new User with username: {}", username);
        userRepository.save(user);

        if (isDoctor) {
            DoctorEntity doctor = new DoctorEntity();
            doctor.setUser(user);
            doctor.setName(user.getName());
            doctor.setSurname(user.getSurname());
            doctor.setBirth(user.getBirth());
            doctor.setEmail(user.getEmail());
            doctorRepo.save(doctor);
        }
        return true;
    }

    public List<UserEntity> list() {
        return (List<UserEntity>) userRepository.findAll();
    }

    //метод поиска
    public List<UserEntity> getUsersByRole(Role role, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            List<UserEntity> usersByName = userRepository.findByRolesAndNameStartingWithIgnoreCase(role, keyword);
            List<UserEntity> usersBySurname = userRepository.findByRolesAndSurnameStartingWithIgnoreCase(role, keyword);
            List<UserEntity> combinedUsers = new ArrayList<>();
            combinedUsers.addAll(usersByName);
            combinedUsers.addAll(usersBySurname);
            return combinedUsers;
        }
        //нету пользователя - возвращаем все что есть
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toList());
    }



    public void banUser(Long id) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if(user.isActive()) {
                user.setActive(false);
                log.info("Ban user with id = {}; username: {}", user.getIduser(), user.getUsername());
            } else {
                user.setActive(true);
                log.info("Unban user with id = {}; username: {}", user.getIduser(), user.getUsername());
            }
        }
        userRepository.save(user);
    }

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //Получаем объект аутентификации из контекста безопасности
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal(); //Получаем объект principal, который представляет аутентифицированного пользователя
        // он предоставляет учетные данные пользователя
        //Проверяем, является ли principal экземпляром класса UserDetails
        // UserDetails - предоставляет основную информацию об аутентифицированном пользователе
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username); //Используем полученное имя пользователя для поиска соответствующего объекта UserEntity в репозитории
        }

        return null;
    }

    @Transactional
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null); //если пользователь с таким id не найден - вернет null
    }



    private ImageUserEntity toImageEntity(MultipartFile file1) throws IOException {
        ImageUserEntity images = new ImageUserEntity();
        images.setName(file1.getName());
        images.setBytes(file1.getBytes());
        return images;
    }


    @Transactional
    public void updateUser(UserEntity userEntity, Long imageId, MultipartFile imageFile) throws IOException {
        // Проверяем, что файл изображения передан
        if (imageFile != null && !imageFile.isEmpty()) {

            // Проверяем, существует ли изображение с заданным id
            Optional<ImageUserEntity> existingImageOptional = imageUserRepo.findById(imageId);

            // Если изображение существует, обновляем его
            if (existingImageOptional.isPresent()) {
                ImageUserEntity existingImage = existingImageOptional.get();
                updateImage(existingImage, imageFile);
            } else {
                // Если изображение с указанным id не существует, сохраняем новое изображение для пользователя
                ImageUserEntity newImage = toImageEntity(imageFile); // Преобразуем MultipartFile в сущность ImageUserEntity
                imageUserRepo.save(newImage); // Сохраняем новое изображение в базе данных
                userEntity.addImageToUser(newImage); // Связываем изображение с пользователем
            }
        }

        // Получаем существующего пользователя из базы данных
        Optional<UserEntity> existingUserOptional = userRepository.findById(userEntity.getIduser());
        if (existingUserOptional.isPresent()) {
            UserEntity existingUser = existingUserOptional.get();
            // Обновляем поля пользователя
            existingUser.setName(userEntity.getName());
            existingUser.setSurname(userEntity.getSurname());
            existingUser.setBirth(userEntity.getBirth());
            existingUser.setPhone(userEntity.getPhone());
            existingUser.setAddress(userEntity.getAddress());
            existingUser.setEmail(userEntity.getEmail());

            // Если новое изображение было добавлено или обновлено, устанавливаем его для пользователя
            if (imageFile != null && !imageFile.isEmpty()) {
                Optional<ImageUserEntity> updatedImageOptional = imageUserRepo.findById(imageId);
                updatedImageOptional.ifPresent(existingUser::addImageToUser); // Устанавливаем обновленное изображение для пользователя
            }

            // Сохраняем обновленного пользователя
            userRepository.save(existingUser);
        } else {
            // Если пользователя не найдено в базе данных, можно сгенерировать исключение или выполнить другие действия в зависимости от требований проекта
            throw new EntityNotFoundException("User with id " + userEntity.getIduser() + " not found");
        }
    }




    private void updateImage(ImageUserEntity image, MultipartFile newImageFile) throws IOException {
        image.setBytes(newImageFile.getBytes()); // Обновляем содержимое изображения

        image.setName(newImageFile.getOriginalFilename());
    }



    public UserEntity getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return userRepository.findByUsername(username);
            }
        }
        return null;
    }
}
