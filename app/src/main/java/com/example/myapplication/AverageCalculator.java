package com.example.myapplication;

import java.text.DecimalFormat;

public class AverageCalculator {

    public String calculate(double total_rating, int count){

        double avg_rating = total_rating/count;
        DecimalFormat df = new DecimalFormat("0.00");
        String avg_rating_string = df.format(avg_rating);
        if(count != 0) {
            return("Average rating: " + avg_rating_string + " stars");
        }
        else{
            return("Average rating: None");
        }
    }
}
