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
public class CheckGingerRogersInfoTest {

    public static final String STRING_TO_BE_DISPLAYED_TITLE_GINGER = "Ginger Rogers Beach";
    public static final String STRING_TO_BE_DISPLAYED_HOURS_GINGER = "4am-12am" + "\n" + "14710 CA-1, Santa Monica, CA 90402";
    public static final String STRING_TO_BE_DISPLAYED_DESCRIPTION_GINGER = "Just south of Entrada and across from Patrick's Roadhouse Grill. Here, the beach tends to be less crowded and always has a good vibe. The recent rating on the water quality is A+, and there are a nearby lifeguard stand and public restrooms.";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes.
     */

    static double[] h_loc = new double[]{34.0261, -118.5183};
    static double[] home = new double[]{34.031330, -118.288420};

    static Intent ginger_intent;

    static {
        ginger_intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        ginger_intent.putExtra("beach_name", "will rogers");
        ginger_intent.putExtra("user", "m@r.com");
        ginger_intent.putExtra("h_loc", h_loc);
        ginger_intent.putExtra("home", home);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> checkGingerScenarioRule
            = new ActivityScenarioRule<>(ginger_intent);

    private Activity getGingerActivity() {
        final Activity[] a = new Activity[1];
        checkGingerScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    @Test
    public void CheckGingerRogers() {

        TextView name_view = getGingerActivity().findViewById(R.id.beach_title);
        String name = name_view.getText().toString();
        while (name.equals("")) {
            name_view = getGingerActivity().findViewById(R.id.beach_title);
            name = name_view.getText().toString();
        }

        onView(withId(R.id.beach_title)).check(matches(withText(STRING_TO_BE_DISPLAYED_TITLE_GINGER)));
        onView(withId(R.id.hours)).check(matches(withText(STRING_TO_BE_DISPLAYED_HOURS_GINGER)));
        onView(withId(R.id.description)).check(matches(withText(STRING_TO_BE_DISPLAYED_DESCRIPTION_GINGER)));
    }

}

