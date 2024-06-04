package com.example.medicine.Controllers;

import com.example.medicine.Entity.*;
import com.example.medicine.service.AppointmentService;
import com.example.medicine.service.DoctorService;
import com.example.medicine.service.ServiceService;
import com.example.medicine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private DoctorService doctorService;


    @GetMapping("/online-appointment")
    public String showNewAppointmentForm( //извлечение параметра запроса из URL
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            Model model) {

        Appointment appointment = new Appointment(); // Создаем нового объекта Appointment

        // Если выбрана конкретная услуга (serviceId), выбираем соответствующего специалиста
        if (serviceId != null) {
            ServiceEntity service = serviceService.getServiceById(serviceId);
            appointment.setService(service);
            // Установка соответствующего специалиста в модель
            model.addAttribute("selectedDoctor", service.getDoctor());
        }

        if (doctorId != null) {
            DoctorEntity doctor = doctorService.getDoctorById(doctorId);
            appointment.setDoctor(doctor);
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("services", serviceService.getAllServices());

        return "user/online-appointment";
    }

    @PostMapping("/appointment/create")
    public String createAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    @RequestParam("serviceId") Long serviceId,
                                    Model model) throws Exception {

        UserEntity user = userService.getCurrentUser(); // Получение пользователя
        appointment.setPatient(user);

        ServiceEntity service = serviceService.getServiceById(serviceId);
        appointment.setService(service);

        DoctorEntity doctor = service.getDoctor(); // Получение доктора, соответствующего выбранной услуге
        appointment.setDoctor(doctor); // Установка выбранного доктора в объект Appointment

        appointment.setStatus(AppointmentStatus.EXPECTATION);
        appointmentService.saveAppointment(appointment);
        return "redirect:/medical-card"; //redirect - переход на другую страницу
    }



    @GetMapping("/getDoctorByService")
    @ResponseBody
    public Long getDoctorByService(@RequestParam("serviceId") Long serviceId) {
        ServiceEntity service = serviceService.getServiceById(serviceId); //получаем информацию о услуге
        if (service != null) {
            return service.getDoctor().getIddoctor(); // Возвращаем id доктора, соответствующего выбранной услуге
        }
        return null;
    }


    @GetMapping("/doctors-by-service/{serviceId}")
    public ResponseEntity<List<DoctorEntity>> getDoctorsByService(@PathVariable Long serviceId) {
        List<DoctorEntity> doctors = doctorService.getDoctorsByServiceId(serviceId);
        return ResponseEntity.ok(doctors);
    }


}
