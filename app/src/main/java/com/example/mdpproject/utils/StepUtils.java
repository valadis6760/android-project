package com.example.mdpproject.utils;


public class StepUtils {

    public int getPercent(int steps, int goal) {
        return ((int) Math.floor(((float) steps / (float) goal) * 100f));
    }

    ;

    public double getCaloriesBurnt(int steps) {
        return ((steps / 20));
    }

    ;

    public double getDistance(int steps, int height) {
        return (((0.414 * height) * steps) / 100000);
    }
}
