package com.example.lunchpicker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LunchController {

    @Autowired
    private LunchService lunchService;

    @GetMapping("/lunch-plan")
    public ResponseEntity<List<LunchPlan>> getLunchPlan(
            @RequestParam int month,
            @RequestParam(required = false) Integer year) {
        
        int currentYear = (year == null) ? LocalDate.now().getYear() : year;

        try {
            List<LunchPlan> plan = lunchService.getLunchPlanForMonth(currentYear, month);
            return ResponseEntity.ok(plan);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<String>> getRestaurants() {
        try {
            List<String> restaurants = lunchService.getRestaurants();
            return ResponseEntity.ok(restaurants);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/restaurants")
    public ResponseEntity<String> updateRestaurants(@RequestBody List<String> restaurants) {
        try {
            lunchService.updateRestaurants(restaurants);
            return ResponseEntity.ok("식당 목록이 성공적으로 업데이트되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("식당 목록 업데이트에 실패했습니다.");
        }
    }
} 