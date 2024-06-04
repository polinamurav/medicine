package com.example.medicine.Controllers;

import com.example.medicine.Entity.UserEntity;
import com.example.medicine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "authorization";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("name", new UserEntity());
        return "registration";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    @PostMapping("/registration")
    public String createUser(@RequestParam(value = "isDoctor", required = false, defaultValue = "false") boolean isDoctor,
                             UserEntity user, Model model) {
        userService.createUser(user, isDoctor);
        return "redirect:/login";
    }

}
