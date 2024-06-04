package com.example.medicine.Controllers;

import com.example.medicine.Entity.Appointment;
import com.example.medicine.Entity.DoctorEntity;
import com.example.medicine.Entity.UserEntity;
import com.example.medicine.model.Week;
import com.example.medicine.model.Day;
import com.example.medicine.model.Calendar;
import com.example.medicine.service.AppointmentService;
import com.example.medicine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

    @Autowired
    private AppointmentService appointmentService;

    private final UserService userService;

    public CalendarController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/doctor/calendar")
    public String doctorCalendar(HttpServletRequest request, Model model) {
        UserEntity currentUser = userService.getCurrentUser(); //получаем текущего пользователя
        DoctorEntity doctor = currentUser.getDoctor(); //извлекаем у него информацию о враче
        model.addAttribute("doctor", doctor);

        //Получается список записей для данного врача и сортируется по дате назначения.
        List<Appointment> appointments = doctor.getAppointmentsList();
        Collections.sort(appointments, Comparator.comparing(Appointment::getAppointmentDate));

        //Список группируется по датам
        //Результат операции группировки сохраняется в Map, где ключом является дата назначения, а значением - список назначений, соответствующих этой дате
        Map<LocalDate, List<Appointment>> appointmentsByDay = appointments.stream()
                .collect(Collectors.groupingBy(appointment -> appointment.getAppointmentDate().toLocalDate()));
        model.addAttribute("appointmentsByDay", appointmentsByDay);

        // Создание объекта календаря для июня 2024 года
        int year = 2024;
        int month = 6;
        Calendar calendar = createCalendar(year, month);
        model.addAttribute("calendar", calendar);


        // Форматирование всех дат и добавление их в модель
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        Map<LocalDate, String> formattedDates = new HashMap<>(); //Создается новый объект HashMap, где ключом будет LocalDate, а значением - отформатированная строка даты.
        calendar.getWeeks().forEach(week -> {
            week.getDays().forEach(day -> {
                LocalDate date = day.getDate();
                if (date != null) {
                    formattedDates.put(date, date.format(formatter));
                }
            });
        });
        model.addAttribute("formattedDates", formattedDates);
        model.addAttribute("currentPath", request.getRequestURI());

        return "doctor/doctor-calendar";
    }




    private Calendar createCalendar(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        List<Week> weeks = new ArrayList<>();
        List<Day> days = new ArrayList<>();

        // Заполнение дней до начала месяца для первой недели
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
        int firstDayValue = firstDayOfWeek.getValue(); // 1=Monday, ... , 7=Sunday

        for (int i = 1; i < firstDayValue; i++) {
            days.add(new Day(null)); // Пустые дни до начала месяца
        }

        // Заполнение дней месяца
        LocalDate currentDay = firstDayOfMonth;
        while (!currentDay.isAfter(lastDayOfMonth)) {
            days.add(new Day(currentDay));
            if (days.size() == 7) {
                weeks.add(new Week(days));
                days = new ArrayList<>();
            }
            currentDay = currentDay.plusDays(1);
        }

        // Заполнение оставшихся дней недели после конца месяца
        if (!days.isEmpty()) {
            while (days.size() < 7) {
                days.add(new Day(null)); // Пустые дни после конца месяца
            }
            weeks.add(new Week(days));
        }

        return new Calendar(weeks);
    }

}
