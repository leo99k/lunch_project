package com.example.lunchpicker;

public class LunchPlan {
    private String date;
    private String restaurant;

    public LunchPlan() {}

    public LunchPlan(String date, String restaurant) {
        this.date = date;
        this.restaurant = restaurant;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getRestaurant() {
        return restaurant;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }
} 