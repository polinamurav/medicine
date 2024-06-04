package com.example.medicine.Controllers;

import com.example.medicine.Entity.Appointment;
import com.example.medicine.Entity.Role;
import com.example.medicine.Entity.UserEntity;
import com.example.medicine.service.AppointmentService;
import com.example.medicine.service.UserService;
import com.example.medicine.Entity.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/admin/users")
    public String adminUsers(HttpServletRequest request, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        UserEntity currentUser = userService.getCurrentUser(); // Получение пользователя
        model.addAttribute("user", currentUser);  // Установка пользователя в модель

        List<UserEntity> usersList = userService.getUsersByRole(Role.ROLE_USER, keyword); // Получение списка пользователей с ролью "ROLE_USER"
        model.addAttribute("usersList", usersList); // Передаем только пользователей с ролью "ROLE_USER"

        model.addAttribute("currentPath", request.getRequestURI()); //чтобы при таком пути горела нужная кнопка сбоку
        return "admin/admin-users";
    }

    @PostMapping("/admin-user-ban")
    public String userBan(@RequestParam("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/report")
    public String getAppointmentStatistics(HttpServletRequest request, Model model) {
        List<Object[]> statistics = appointmentService.getAppointmentStatusStatistics(); //получение статистики по статусу записи
        List<Object[]> serviceStatistics = appointmentService.getServiceStatistics(); //статистика по услугам
        List<Object[]> timeStatistics = appointmentService.getTimeStatistics(); //статистика по времени
        model.addAttribute("statistics", statistics);
        model.addAttribute("serviceStatistics", serviceStatistics);
        model.addAttribute("timeStatistics", timeStatistics);

        model.addAttribute("currentPath", request.getRequestURI()); //чтобы при таком пути горела нужная кнопка сбоку
        return "admin/admin-report";
    }


}
