package com.example.beta;

import androidx.annotation.NonNull;

public class Day {
    private String Breakfast,Snack1,Lunch, Snack2,Dinner;

    public Day(String Breakfast, String Dinner, String Lunch, String Snack1, String Snack2){
        this.Breakfast=Breakfast;
        this.Snack1=Snack1;
        this.Lunch=Lunch;
        this.Snack2=Snack2;
        this.Dinner=Dinner;




    }

    public Day(){
        Breakfast = "";
        Dinner = "";
        Lunch = "";
        Snack1 = "";
        Snack2 = "";
    }

    public String getSnack2() {
        return Snack2;
    }

    public void setSnack2(String snack2) {
        Snack2 = snack2;
    }

    public String getSnack1() {
        return Snack1;
    }

    public String getLunch() {
        return Lunch;
    }

    public String getDinner() {
        return Dinner;
    }

    public String getBreakfast() {
        return Breakfast;
    }

    public void setSnack1(String snack1) {
        Snack1 = snack1;
    }

    public void setLunch(String lunch) {
        Lunch = lunch;
    }

    public void setDinner(String dinner) {
        Dinner = dinner;
    }

    public void setBreakfast(String breakfast) {
        Breakfast = breakfast;
    }

    @NonNull
    @Override
    public String toString() {
        return Breakfast + "\n"+Snack1+"\n"+Lunch+"\n"+Snack2+"\n"+Dinner;
    }
}
