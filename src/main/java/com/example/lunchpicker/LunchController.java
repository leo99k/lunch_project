package com.example.lunchpicker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
} 