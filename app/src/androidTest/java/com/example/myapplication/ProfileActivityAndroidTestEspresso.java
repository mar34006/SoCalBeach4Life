package com.example.myapplication;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileActivityAndroidTestEspresso {
    @Rule
    public ActivityScenarioRule<ProfileActivity> activityScenarioRule
            = new ActivityScenarioRule<>(ProfileActivity.class);

    private View decorView;

    private Activity getCurrentActivity() {
        final Activity[] a = new Activity[1];
        activityScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    private void login()
    {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
        onView(withId(R.id.email))
                .perform(typeText("j@gmail.om"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Intents.init();
        Boolean done = false;
        while(!done)
        {
            ProgressBar PB = (ProgressBar) getCurrentActivity().findViewById(R.id.progressBar);
            if(PB.getVisibility() == View.GONE)
            {
                done = true;
            }
        }

        intended(hasComponent(DisplayBeachesActivity.class.getName()));
        Intents.release();
    }

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity ->
        {
            decorView = activity.getWindow().getDecorView();
        });
    }

    @Test
    public void clickingUpdateWithoutModifying() {
        login();
        onView(withId(R.id.profilePage)).perform(click());

    }

    @Test
    public void modifyingFirstName() {

    }

    @Test
    public void modifyingLastName() {

    }

    @Test
    public void modifyingAddress() {

    }


}
