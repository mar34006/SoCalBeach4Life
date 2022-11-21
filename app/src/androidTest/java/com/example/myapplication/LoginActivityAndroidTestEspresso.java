package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.core.content.ContextCompat;
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
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.intent.Intents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityAndroidTestEspresso
{
    @Rule public ActivityScenarioRule<LoginActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);
    private Activity getCurrentActivity() {
        final Activity[] a = new Activity[1];
        activityScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    private View decorView;

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity ->
            {
                decorView = activity.getWindow().getDecorView();
            });
    }

    @Test
    public void Test_Login() {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
        onView(withId(R.id.email))
                .perform(typeText("g@f.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.email)).check(matches(withText("g@f.com")));
        onView(withId(R.id.signIn)).perform(click());
        Intents.init();
        Boolean done = false;
        while(!done)
        {
            ProgressBar PB = (ProgressBar) getCurrentActivity().findViewById(R.id.progressBar);
            Log.i("Test", String.valueOf(PB.getVisibility()));
            if(PB.getVisibility() == View.GONE)
            {
                done = true;
            }
        }
        // go to DisplayBeaches
        intended(hasComponent(DisplayBeachesActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void Incorrect_Login() {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
        onView(withId(R.id.email))
                .perform(typeText("g@f.com"), closeSoftKeyboard());
        onView(withId(R.id.password)) // wrong password
                .perform(typeText("123457"), closeSoftKeyboard());
        onView(withId(R.id.email)).check(matches(withText("g@f.com")));
        onView(withId(R.id.signIn)).perform(click());
        Boolean done = false;
        while(!done)
        {
            ProgressBar PB = (ProgressBar) getCurrentActivity().findViewById(R.id.progressBar);
            Log.i("Test", String.valueOf(PB.getVisibility()));
            if(PB.getVisibility() == View.GONE)
            {
                done = true;
            }
        }
        // no intents
        // instead check toast appears
        onView(withText(R.string.login_failed_toast_text)).inRoot(withDecorView(Matchers.not(decorView)));
    }
}
