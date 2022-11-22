package com.example.myapplication;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static java.lang.Thread.sleep;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileActivityAndroidTestEspresso {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    private View decorView;
    private UiDevice device;

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
        final Instrumentation inst = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(inst);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void clickingOnBannerTakesUserBack() {
        login();
        onView(withId(R.id.profilePage)).perform(click());
        onView(withId(R.id.banner)).perform(click());

        intended(hasComponent(DisplayBeachesActivity.class.getName()));

    }

    @Test
    public void bannerIsAlwaysBelowBackButton() {
        login();
        onView(withId(R.id.profilePage)).perform(click());
        onView(withText("SoCalBeach4Life")).check(isCompletelyBelow(withId(R.id.back)));

    }

    @Test
    public void clickingUpdateWithoutModifying() {
        login();
        onView(withId(R.id.profilePage)).perform(click());
        onView(withId(R.id.updateInfo)).perform(click());

        intended(hasComponent(ProfileActivity.class.getName()));
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

    @Test
    public void saveTripWithoutClickingLot() {
        login();

        //somehow click on a beach icon
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        onView(withId(R.id.editTextTextPostalAddress)).perform(typeText("USC"), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        onView(withText("Address loaded!")).inRoot(withDecorView(Matchers.not(decorView)));

        UiObject beach_marker = device.findObject(new UiSelector().descriptionContains("Venice Beach"));
        try{
            beach_marker.click();
        } catch (Exception e) {}
        onView(withId(R.id.beach_go)).perform(click());
        onView(withId(R.id.route)).perform(click());
        onView(withId(R.id.saveRoute)).perform(click());

        onView(withText("Select a lot to begin routing")).inRoot(withDecorView(Matchers.not(decorView)));
    }

    @Test
    public void saveTripIsPossible() {
        login();

        //somehow click on a beach icon
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        onView(withId(R.id.editTextTextPostalAddress)).perform(typeText("USC"), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        onView(withText("Address loaded!")).inRoot(withDecorView(Matchers.not(decorView)));

        UiObject beach_marker = device.findObject(new UiSelector().descriptionContains("Venice Beach"));
        try{
            beach_marker.click();
        } catch (Exception e) {}
        onView(withId(R.id.beach_go)).perform(click());
        onView(withId(R.id.route)).perform(click());

        //somehow click on a lot icon
        beach_marker = device.findObject(new UiSelector().descriptionContains("Lot"));
        try{
            beach_marker.click();
        } catch (Exception e) {}

        onView(withId(R.id.saveRoute)).perform(click());

        onView(withText("Route Saved!")).inRoot(withDecorView(Matchers.not(decorView)));
    }

    @Test
    public void saveTripCorrectInformation() {
        login();

        //somehow click on a beach icon
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        onView(withId(R.id.editTextTextPostalAddress)).perform(typeText("USC"), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        onView(withText("Address loaded!")).inRoot(withDecorView(Matchers.not(decorView)));

        UiObject beach_marker = device.findObject(new UiSelector().descriptionContains("Venice Beach"));
        try{
            beach_marker.click();
        } catch (Exception e) {}
        onView(withId(R.id.beach_go)).perform(click());
        onView(withId(R.id.route)).perform(click());

        //somehow click on a lot icon
        beach_marker = device.findObject(new UiSelector().descriptionContains("Lot 1"));
        try{
            beach_marker.click();
        } catch (Exception e) {}

        onView(withId(R.id.saveRoute)).perform(click());
        pressBack();
        pressBack();
        onView(withId(R.id.profilePage)).perform(click());
        onView(withId(R.id.viewRoutes)).perform(click());

        onView(withText("Destination: Venice Beach")).inRoot(withDecorView(Matchers.not(decorView)));
    }


}
