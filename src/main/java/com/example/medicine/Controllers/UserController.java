package com.example.medicine.Controllers;

import com.example.medicine.Entity.Appointment;
import com.example.medicine.Entity.AppointmentStatus;
import com.example.medicine.Entity.UserEntity;
import com.example.medicine.service.AppointmentService;
import com.example.medicine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AppointmentService appointmentService;

    @GetMapping("/online-consultation")
    public String consultation(Model model) {
        return "user/online-consultation";
    }

    @GetMapping("/medical-card")
    public String medicalCard(Model model) {
        UserEntity user = userService.getCurrentUser(); // Получение пользователя
        model.addAttribute("user", user);  // Установка пользователя в модель

        List<Appointment> appointments = user.getAppointmentsList(); // Получение списка приемов пользователя

        // Фильтрация записей с ненулевыми датами приема
        List<Appointment> validAppointments = appointments.stream()
                .filter(appointment -> appointment.getAppointmentDate() != null)
                .collect(Collectors.toList());
        // Сортировка записей по дате приема в порядке убывания
        validAppointments.sort(Comparator.comparing(Appointment::getAppointmentDate).reversed());

        model.addAttribute("appointmentList", validAppointments);  // Установка списка приемов в модель
        return "user/medical-card";
    }

    //изменение медицинской карты пользователя
    @GetMapping("/medical-card/edit-acc-user/{id}")
    public String editAccUser(Model model, @PathVariable("id") Long id) {
        UserEntity user = userService.getUserById(id);
        model.addAttribute("user", user);  // Установка пользователя в модель
        return "user/edit-acc-user";
    }

    @PostMapping("/edit-acc-user/{id}")
    public String updateUser(@ModelAttribute("user") UserEntity user, @PathVariable("id") Long id,
                             @RequestParam("file") MultipartFile file) throws IOException {
        userService.updateUser(user, id, file);
        return "redirect:/medical-card";
    }


    //нажатие на кнопку отменить запись
    @PostMapping("/cancel-appointment")
    public String cancelAppointment(@RequestParam("appointmentId") Long appointmentId) throws Exception {
        // Находим запись по ее идентификатору
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        // Если запись найдена, меняем ее статус на "CANCELLED"
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.CANCELED);
            appointmentService.saveAppointment(appointment); // Сохраняем обновленную запись в базу данных
        }

        return "redirect:/medical-card";
    }


}
