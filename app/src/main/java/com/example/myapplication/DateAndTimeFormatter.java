package com.example.myapplication;

import java.util.ArrayList;
import java.util.Calendar;

public class DateAndTimeFormatter {

    public ArrayList<String> get_info(int hours, int minutes, String duration) {

        Calendar currentTime = Calendar.getInstance();
        Calendar after = Calendar.getInstance();
        after.add(Calendar.HOUR_OF_DAY, hours);
        after.add(Calendar.MINUTE, minutes);
        String date = String.format("%d/%d/%d: ", currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH), currentTime.get(Calendar.YEAR));
        String time1 = String.format("%02d:%02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
        String time2 = String.format("%02d:%02d", after.get(Calendar.HOUR_OF_DAY), after.get(Calendar.MINUTE));
        String formattedDuration = time1 + " - " + time2 + " | Duration: " + duration;

        ArrayList<String> return_list = new ArrayList<>();
        return_list.add(date);
        return_list.add(formattedDuration);
        return return_list;

    }
}
