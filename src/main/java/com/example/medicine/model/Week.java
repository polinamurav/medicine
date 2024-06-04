package com.example.medicine.model;

import java.util.List;

public class Week {
    private List<Day> days;

    public Week(List<Day> days) {
        this.days = days;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
