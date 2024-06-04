package com.example.medicine.model;

import java.time.LocalDate;
import java.util.List;

public class Calendar {
        private List<Week> weeks;

        public Calendar(List<Week> weeks) {
            this.weeks = weeks;
        }

        public List<Week> getWeeks() {
            return weeks;
        }

        public void setWeeks(List<Week> weeks) {
            this.weeks = weeks;
        }

}
