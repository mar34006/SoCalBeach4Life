package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

import android.graphics.Typeface;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AverageRatingTests {

    @Test
    public void average_review_is_correct_1() {

        double total_rating = 5;
        int count = 2;

        AverageCalculator calc = new AverageCalculator();
        String display = calc.calculate(total_rating, count);

        assertEquals("Average rating: 2.50 stars", display);

    }

    @Test
    public void average_review_is_correct_2() {

        double total_rating = 13;
        int count = 3;

        AverageCalculator calc = new AverageCalculator();
        String display = calc.calculate(total_rating, count);

        assertEquals("Average rating: 4.33 stars", display);

    }

    @Test
    public void average_review_is_correct_3() {

        double total_rating = 5;
        int count = 0;

        AverageCalculator calc = new AverageCalculator();
        String display = calc.calculate(total_rating, count);

        assertEquals("Average rating: None", display);

    }
}