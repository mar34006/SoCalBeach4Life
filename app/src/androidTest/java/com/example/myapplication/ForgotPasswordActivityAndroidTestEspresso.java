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

public class ForgotPasswordActivityAndroidTestEspresso {
    @Rule
    public ActivityScenarioRule<ForgotPassword> activityScenarioRule
            = new ActivityScenarioRule<>(ForgotPassword.class);

    public final String EMAIL = "j@gmail.com";
    public final String INVALID_EMAIL = "j";
    public final String EMAIL_NOT_IN_DATABASE = "ewqeqwoehi@gmail.com";

    private View decorView;

    private Activity getCurrentActivity() {
        final Activity[] a = new Activity[1];
        activityScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity ->
        {
            decorView = activity.getWindow().getDecorView();
        });
    }


    @Test
    public void bannerIsCompletelyBelowBackButton() {
        onView(withText("SoCalBeach4Life")).check(isCompletelyBelow(withId(R.id.back)));
    }

    @Test
    public void emptyResetPasswordField()
    {
        onView(withId(R.id.resetPassword)).perform(click());
        onView(withId(R.id.email)).check(matches(hasErrorText("Email is required.")));
    }

    @Test
    public void invalidEmailAddress()
    {
        onView(withId(R.id.email)).perform(typeText(INVALID_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.resetPassword)).perform(click());
        onView(withId(R.id.email)).check(matches(hasErrorText("Please provide a valid email.")));
    }

    @Test
    public void emailAddressNotInDatabase()
    {
        onView(withId(R.id.email)).perform(typeText(EMAIL_NOT_IN_DATABASE), closeSoftKeyboard());
        onView(withId(R.id.resetPassword)).perform(click());
        onView(withText("Something went wrong..."))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));
    }


    @Test
    public void sendForgotPasswordEmail()
    {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);

        onView(withId(R.id.email))
                .perform(typeText(EMAIL), closeSoftKeyboard());
        onView(withId(R.id.resetPassword)).perform(click());

        Boolean done = false;
        while(!done)
        {
            ProgressBar PB = (ProgressBar) getCurrentActivity().findViewById(R.id.progressBar);
            if(PB.getVisibility() == View.GONE)
            {
                done = true;
            }
        }

        onView(withText(R.string.forgot_password_successful_toast)).inRoot(withDecorView(Matchers.not(decorView)));

    }

}
