package com.example.medicine.Controllers;

import com.example.medicine.Entity.DoctorEntity;
import com.example.medicine.Entity.UserEntity;
import com.example.medicine.service.DoctorService;
import com.example.medicine.service.ServiceService;
import com.example.medicine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {
    private final UserService userService;
    private final DoctorService doctorService;

    @Autowired
    private ServiceService serviceService;

    public MainController(UserService userService, DoctorService doctorService) {
        this.userService = userService;
        this.doctorService = doctorService;
    }

    @GetMapping("/") //обрабатывает определенный UML адрес, / - главная страница
    public String home(Model model) { //вызываем определенный шаблон. передаем обязательный параметр Model
        // Получение пользователя
        UserEntity user = userService.getCurrentUser();
        // Установка пользователя в модель
        model.addAttribute("user", user);

        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        model.addAttribute("services", serviceService.getAllServices());

        return "index"; //вызываем определенный шаблон по его названию
    }

    @GetMapping("/services")
    public String about(Model model) {
        UserEntity user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "services";
    }

    @GetMapping("/contacts")
    public String contact(Model model) {
        return "contacts";
    }

}
