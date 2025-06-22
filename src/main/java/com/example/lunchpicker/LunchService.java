package com.example.lunchpicker;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LunchService {

    public List<LunchPlan> getLunchPlanForMonth(int year, int month) throws IOException {
        List<String> restaurants = loadRestaurants();
        if (restaurants.isEmpty()) {
            return Collections.emptyList();
        }

        List<LocalDate> workingDays = getWorkingDays(year, month);
        Collections.shuffle(restaurants);

        List<LunchPlan> lunchPlans = new ArrayList<>();
        for (int i = 0; i < workingDays.size(); i++) {
            String restaurant = restaurants.get(i % restaurants.size());
            lunchPlans.add(new LunchPlan(workingDays.get(i).toString(), restaurant));
        }

        return lunchPlans;
    }

    private List<String> loadRestaurants() throws IOException {
        ClassPathResource resource = new ClassPathResource("lunch.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
        }
    }

    private List<LocalDate> getWorkingDays(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<LocalDate> workingDays = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays.add(date);
            }
        }
        return workingDays;
    }
} 