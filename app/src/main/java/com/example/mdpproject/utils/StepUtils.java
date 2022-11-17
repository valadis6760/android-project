package com.example.mdpproject.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class StepUtils {
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public int getPercent(int steps, int goal) {
        return ((int) Math.floor(((float) steps / (float) goal) * 100f));
    };

    public double getCaloriesBurnt(int steps) {
        return (round(steps / 20));
    };

    public double getDistance(int steps, int height) {
        return (((0.414 * height) * steps) / 100000);
    }
    public String getDistanceToString(int steps, int height) {
        return (round(((0.414 * height) * steps) / 100000)+" Km");
    }
}
