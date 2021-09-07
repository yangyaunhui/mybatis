package com.yyh.bean;

public class PersonDto {
    private int gender;
    private double avgScore;

    @Override
    public String toString() {
        return "PersonDto{" +
                "gender=" + gender +
                ", avgScore=" + avgScore +
                '}';
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }
}
