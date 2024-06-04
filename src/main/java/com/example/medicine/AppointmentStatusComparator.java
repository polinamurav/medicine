package com.example.medicine;
import com.example.medicine.Entity.Appointment;
import com.example.medicine.Entity.AppointmentStatus;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

//фильтрация заявок у доктора, чтобы в правильном порядке высвечивались
public class AppointmentStatusComparator implements Comparator<Appointment> {
    @Override
    public int compare(Appointment a1, Appointment a2) {
        // Define the order of statuses
        List<AppointmentStatus> order = Arrays.asList(
                AppointmentStatus.EXPECTATION,
                AppointmentStatus.APPROVED,
                AppointmentStatus.COMPLETED,
                AppointmentStatus.CANCELED
        );

        return Integer.compare(order.indexOf(a1.getStatus()), order.indexOf(a2.getStatus()));
    }
}
