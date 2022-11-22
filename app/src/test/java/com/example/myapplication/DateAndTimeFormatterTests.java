package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

public class DateAndTimeFormatterTests {

    @Test
    public void date_and_time_format_is_correct_1() {

        int hours = 0;
        int minutes = 57;
        String duration = "57 mins";

        DateAndTimeFormatter formatter = new DateAndTimeFormatter();
        ArrayList<String> info = formatter.get_info(hours, minutes, duration);

        Calendar currentTime = Calendar.getInstance();
        String date = String.format("%d/%d/%d: ", currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH), currentTime.get(Calendar.YEAR));
        assertEquals(date, info.get(0));

        Calendar after = Calendar.getInstance();
        after.add(Calendar.HOUR_OF_DAY, hours);
        after.add(Calendar.MINUTE, minutes);
        String time1 = String.format("%02d:%02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
        String time2 = String.format("%02d:%02d", after.get(Calendar.HOUR_OF_DAY), after.get(Calendar.MINUTE));
        String trip_duration = time1 + " - " + time2 + " | Duration: " + duration;
        assertEquals(trip_duration, info.get(1));

    }

    @Test
    public void date_and_time_format_is_correct_2() {

        int hours = 1;
        int minutes = 17;
        String duration = "1 hour 17 mins";

        DateAndTimeFormatter formatter = new DateAndTimeFormatter();
        ArrayList<String> info = formatter.get_info(hours, minutes, duration);

        Calendar currentTime = Calendar.getInstance();
        String date = String.format("%d/%d/%d: ", currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH), currentTime.get(Calendar.YEAR));
        assertEquals(date, info.get(0));

        Calendar after = Calendar.getInstance();
        after.add(Calendar.HOUR_OF_DAY, hours);
        after.add(Calendar.MINUTE, minutes);
        String time1 = String.format("%02d:%02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
        String time2 = String.format("%02d:%02d", after.get(Calendar.HOUR_OF_DAY), after.get(Calendar.MINUTE));
        String trip_duration = time1 + " - " + time2 + " | Duration: " + duration;
        assertEquals(trip_duration, info.get(1));

    }

}
