package com.example.medicine.Controllers;

import com.example.medicine.AppointmentStatusComparator;
import com.example.medicine.Entity.*;
import com.example.medicine.service.AppointmentService;
import com.example.medicine.service.DoctorService;
import com.example.medicine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_DOCTOR')")
public class DoctorController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;


    @GetMapping("/doctor/doctor-main")
    public String doctorMain(HttpServletRequest request, Model model) {
        UserEntity currentUser = userService.getCurrentUser();
        DoctorEntity doctor = currentUser.getDoctor();
        model.addAttribute("doctor", doctor);

        List<Appointment> appointments = doctor.getAppointmentsList(); // Получение списка приемов доктора

        Collections.sort(appointments, new AppointmentStatusComparator()); // Сортировка списка заявок
        model.addAttribute("appointmentList", appointments);  // Установка списка приемов в модель

        model.addAttribute("currentPath", request.getRequestURI()); //чтобы при таком пути горела нужная кнопка сбоку

        return "doctor/doctor-main";
    }


    //нажатие на кнопку отменить запись у доктора
    @PostMapping("/doctor/cancel-appointment")
    public String cancelAppointment(@RequestParam("appointmentId") Long appointmentId) throws Exception {
        // Находим запись по ее идентификатору
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        // Если запись найдена, меняем ее статус на "CANCELLED"
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.CANCELED);
            appointmentService.saveAppointment(appointment); // Сохраняем обновленную запись в базу данных
        }

        return "redirect:/doctor/doctor-main";
    }

    //нажатие на кнопку подтвердить запись у доктора
    @PostMapping("/doctor/approve-appointment")
    public String approveAppointment(@RequestParam("appointmentId") Long appointmentId) throws Exception {
        // Находим запись по ее идентификатору
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        // Если запись найдена, меняем ее статус на "APPROVED"
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.APPROVED);
            appointmentService.saveAppointment(appointment); // Сохраняем обновленную запись в базу данных
        }

        return "redirect:/doctor/doctor-main";
    }



    @GetMapping("/doctor/users-name")
    public String nameUsers(HttpServletRequest request, @RequestParam(name = "keyword", required = false) String keyword, Model model) { //name=keyword это мы пишем в поиске такой же
        UserEntity user = userService.getCurrentUser(); // Получение пользователя
        model.addAttribute("user", user);  // Установка пользователя в модель
        model.addAttribute("usersList", userService.getUsersByRole(Role.ROLE_USER, keyword)); // Передаем только пользователей с ролью "ROLE_USER"
        model.addAttribute("currentPath", request.getRequestURI()); //чтобы при таком пути горела нужная кнопка сбоку
        return "doctor/doctor-users-name";
    }

    //просмотр медицинской карты пользователя
    @GetMapping("/doctor/medical-card")
    public String showMedicalCard(@RequestParam(name = "id") Long id, Model model) {
        UserEntity user = userService.getUserById(id);
        model.addAttribute("user", user);  // Установка пользователя в модель
        return "doctor/doctor-user-medical-card";
    }


    @GetMapping("/doctor/all-appointments")
    public String allAppointments(HttpServletRequest request, @RequestParam(name = "keyword", required = false) String keyword, Model model) { //name=keyword это мы пишем в поиске такой же
        UserEntity currentUser = userService.getCurrentUser(); // Получение пользователя
        model.addAttribute("user", currentUser);  // Установка пользователя в модель

        List<UserEntity> usersList = userService.getUsersByRole(Role.ROLE_USER, keyword); // Получение списка пользователей с ролью "ROLE_USER"

        model.addAttribute("usersList", usersList); // Передаем только пользователей с ролью "ROLE_USER"


        // Собираем все записи всех пользователей
        List<Appointment> allAppointments = usersList.stream()
                .flatMap(user -> user.getAppointmentsList().stream())
                .filter(appointment -> appointment.getAppointmentDate() != null)
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .collect(Collectors.toList());

        model.addAttribute("appointmentList", allAppointments);

        model.addAttribute("currentPath", request.getRequestURI()); //чтобы при таком пути горела нужная кнопка сбоку
        return "doctor/all-appointments";
    }


    //редактирование аккаунта доктора
    @GetMapping("/doctor/edit-acc/{id}")
    public String editAccDoctor(Model model, @PathVariable("id") Long id) {
        DoctorEntity doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "doctor/doctor-edit-account";
    }

    @PostMapping("/doctor/edit-acc/{id}")
    public String updateDoctor(@ModelAttribute("doctor") DoctorEntity doctor, @PathVariable("id") Long id,
                               @RequestParam("file") MultipartFile file) throws IOException {
        doctorService.updateDoctor(doctor, id, file);
        return "redirect:/doctor/doctor-main";
    }



    @PostMapping("/add-illnesses/{id}")
    public String processAddIllnesses(@ModelAttribute("user") UserEntity user, @PathVariable("id") Long id) {
        UserEntity existingUser = userService.getUserById(id); //Получаем пользователя по его идентификатору

        existingUser.setDisease_history(user.getDisease_history()); // Обновляем данные о болезнях пользователя

        userService.saveUser(existingUser); // Сохраняем обновленные данные в базу данных

        return "redirect:/doctor/users-name";
    }



}
