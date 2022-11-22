package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class CheckHermosaInfoTest {

    public static final String STRING_TO_BE_DISPLAYED_TITLE = "Hermosa Beach";
    public static final String STRING_TO_BE_DISPLAYED_HOURS = "6am-12am" + "\n" + "1001-1099 The Strand, Hermosa Beach, CA 90254";
    public static final String STRING_TO_BE_DISPLAYED_DESCRIPTION = "Hermosa Beach has almost 2 miles of ocean frontage with 94 acres of public beach and is one of the most popular beaches in Los Angeles County due to the excellent surf, good swimming areas, and the various volleyball nets along the sand.";

    static double[] h_loc = new double[]{34.0261, -118.5183};
    static double[] home = new double[]{34.031330, -118.288420};

    static Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("beach_name", "hermosa beach");
        intent.putExtra("user", "m@r.com");
        intent.putExtra("h_loc", h_loc);
        intent.putExtra("home", home);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> checkBeachScenarioRule
            = new ActivityScenarioRule<>(intent);

    private Activity getHermosaActivity() {
        final Activity[] a = new Activity[1];
        checkBeachScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    @Test
    public void CheckHermosa() {

        TextView name_view = getHermosaActivity().findViewById(R.id.beach_title);
        String name = name_view.getText().toString();
        while (name.equals("")) {
            name_view = getHermosaActivity().findViewById(R.id.beach_title);
            name = name_view.getText().toString();
        }

        onView(withId(R.id.beach_title)).check(matches(withText(STRING_TO_BE_DISPLAYED_TITLE)));
        onView(withId(R.id.hours)).check(matches(withText(STRING_TO_BE_DISPLAYED_HOURS)));
        onView(withId(R.id.description)).check(matches(withText(STRING_TO_BE_DISPLAYED_DESCRIPTION)));
    }
}
