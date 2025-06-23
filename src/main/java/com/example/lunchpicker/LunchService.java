package com.example.lunchpicker;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LunchService {

    private static final String FIXED_PLAN_FILE_FORMAT = "lunch-fixed-%d-%02d.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<LunchPlan> getLunchPlanForMonth(int year, int month) throws IOException {
        // 1. 확정된 계획이 있으면 반환
        List<LunchPlan> fixed = loadFixedPlan(year, month);
        if (fixed != null) return fixed;
        // 2. 없으면 랜덤 생성
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

    public List<String> getRestaurants() throws IOException {
        return loadRestaurants();
    }

    public void updateRestaurants(List<String> restaurants) throws IOException {
        ClassPathResource resource = new ClassPathResource("lunch.txt");
        Path filePath = Paths.get(resource.getURI());
        
        // 빈 줄을 제거하고 저장
        List<String> cleanRestaurants = restaurants.stream()
                .map(String::trim)
                .filter(restaurant -> !restaurant.isEmpty())
                .collect(Collectors.toList());
        
        Files.write(filePath, cleanRestaurants, StandardCharsets.UTF_8);
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

    public void fixLunchPlan(int year, int month, List<LunchPlan> plan) throws IOException {
        String filePath = String.format(FIXED_PLAN_FILE_FORMAT, year, month);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), plan);
    }

    public void unfixLunchPlan(int year, int month) {
        String filePath = String.format(FIXED_PLAN_FILE_FORMAT, year, month);
        File file = new File(filePath);
        if (file.exists()) file.delete();
    }

    public boolean isFixed(int year, int month) {
        String filePath = String.format(FIXED_PLAN_FILE_FORMAT, year, month);
        return new File(filePath).exists();
    }

    private List<LunchPlan> loadFixedPlan(int year, int month) throws IOException {
        String filePath = String.format(FIXED_PLAN_FILE_FORMAT, year, month);
        File file = new File(filePath);
        if (!file.exists()) return null;
        return objectMapper.readValue(file, new TypeReference<List<LunchPlan>>(){});
    }
} 