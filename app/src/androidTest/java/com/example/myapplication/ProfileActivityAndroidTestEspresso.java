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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileActivityAndroidTestEspresso {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

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
                .perform(typeText("j@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());

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
    }

    @Before
    public void setUp() {
        Intents.init();
        activityScenarioRule.getScenario().onActivity(activity ->
        {
            decorView = activity.getWindow().getDecorView();
        });
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void clickingUpdateWithoutModifying() {
        login();
        onView(withId(R.id.profilePage)).perform(click());
        onView(withId(R.id.updateInfo)).perform(click());

    }

    @Test
    public void modifyingFirstName() {
        login();
        onView(withId(R.id.profilePage)).perform(click());

        onView(withId(R.id.firstName))
                .perform(typeText("e"), closeSoftKeyboard());

        onView(withId(R.id.updateInfo)).perform(click());
        onView(withText(R.string.data_updated_successful_toast)).inRoot(withDecorView(Matchers.not(decorView)));

    }

    @Test
    public void modifyingLastName() {
        login();
        onView(withId(R.id.profilePage)).perform(click());

        onView(withId(R.id.lastName))
                .perform(typeText("e"), closeSoftKeyboard());

        onView(withId(R.id.updateInfo)).perform(click());
        onView(withText(R.string.data_updated_successful_toast)).inRoot(withDecorView(Matchers.not(decorView)));
    }

    @Test
    public void modifyingAddress() {
        login();
        onView(withId(R.id.profilePage)).perform(click());

        onView(withId(R.id.address))
                .perform(typeText("e"), closeSoftKeyboard());

        onView(withId(R.id.updateInfo)).perform(click());
        onView(withText(R.string.data_updated_successful_toast)).inRoot(withDecorView(Matchers.not(decorView)));
    }


}
