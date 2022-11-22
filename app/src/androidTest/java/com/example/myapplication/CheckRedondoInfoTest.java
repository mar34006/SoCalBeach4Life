package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckRedondoInfoTest {

    public static final String STRING_TO_BE_DISPLAYED_TITLE= "Redondo Beach";
    public static final String STRING_TO_BE_DISPLAYED_HOURS = "12am-12am" + "\n" + "Fisherman's Wharf, Fishermans Wharf, Redondo Beach, CA 90277";
    public static final String STRING_TO_BE_DISPLAYED_DESCRIPTION = "Redondo Beach is great for swimming, surfing, and windsurfing and the horseshoe-shaped pier is good for fishing and has many restaurants and shops.";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes.
     */

    static double[] h_loc = new double[]{34.0261, -118.5183};
    static double[] home = new double[]{34.031330, -118.288420};

    static Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("beach_name", "redondo beach");
        intent.putExtra("user", "m@r.com");
        intent.putExtra("h_loc", h_loc);
        intent.putExtra("home", home);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> checkBeachScenarioRule
            = new ActivityScenarioRule<>(intent);

    private Activity getRedondoActivity() {
        final Activity[] a = new Activity[1];
        checkBeachScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    @Test
    public void CheckRedondo() {

        TextView name_view = getRedondoActivity().findViewById(R.id.beach_title);
        String name = name_view.getText().toString();
        while (name.equals("")) {
            name_view = getRedondoActivity().findViewById(R.id.beach_title);
            name = name_view.getText().toString();
        }

        onView(withId(R.id.beach_title)).check(matches(withText(STRING_TO_BE_DISPLAYED_TITLE)));
        onView(withId(R.id.hours)).check(matches(withText(STRING_TO_BE_DISPLAYED_HOURS)));
        onView(withId(R.id.description)).check(matches(withText(STRING_TO_BE_DISPLAYED_DESCRIPTION)));
    }

}
