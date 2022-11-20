package com.example.myapplication;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
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
public class RegisterActivityAndroidTestEspresso
{
    @Rule public ActivityScenarioRule<RegisterUser> activityScenarioRule
            = new ActivityScenarioRule<>(RegisterUser.class);

    public final String FNAME = "G";
    public final String LNAME = "V";
    public final String EMAIL = "g@r.com";
    public final String ADDR = "234 Main St.";
    public final String PASS = "hithere345";

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
    public void Test_Register() {
        // Type text and then press the button.
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
        onView(withId(R.id.firstName))
                .perform(typeText(FNAME), closeSoftKeyboard());
        onView(withId(R.id.lastName))
                .perform(typeText(LNAME), closeSoftKeyboard());
        onView(withId(R.id.email))
                .perform(typeText(EMAIL), closeSoftKeyboard());
        onView(withId(R.id.address))
                .perform(typeText(ADDR), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(PASS), closeSoftKeyboard());
        onView(withId(R.id.registerUser)).perform(click());
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
        intended(hasComponent(LoginActivity.class.getName()));
        onView(withText(R.string.register_successful_toast)).inRoot(withDecorView(Matchers.not(decorView)));
    }

    @Test
    public void Try_Empty_Register() {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
        // try an empty register
        onView(withId(R.id.registerUser)).perform(click());
        onView(withId(R.id.firstName)).check(matches(hasErrorText("First name is required.")));
    }


    @Test
    public void Try_Reregister() {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(getCurrentActivity(), com.google.firebase.database.collection.R.drawable.common_google_signin_btn_text_light_normal_background);
        ((ProgressBar) getCurrentActivity().findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
        onView(withId(R.id.firstName))
                .perform(typeText(FNAME), closeSoftKeyboard());
        onView(withId(R.id.lastName))
                .perform(typeText(LNAME), closeSoftKeyboard());
        onView(withId(R.id.email))
                .perform(typeText(EMAIL), closeSoftKeyboard());
        onView(withId(R.id.address))
                .perform(typeText(ADDR), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(PASS), closeSoftKeyboard());
        onView(withId(R.id.registerUser)).perform(click());
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
        onView(withText(R.string.register_failure_toast)).inRoot(withDecorView(Matchers.not(decorView)));
    }
}
