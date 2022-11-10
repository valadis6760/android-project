package com.example.mdpproject.db;

public class MonthlyInfo implements Comparable<MonthlyInfo>{

    private int month;
    private int steps;


    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @Override
    public int compareTo(MonthlyInfo monthlyInfo) {
        return monthlyInfo.getMonth() - this.month;
    }
}
