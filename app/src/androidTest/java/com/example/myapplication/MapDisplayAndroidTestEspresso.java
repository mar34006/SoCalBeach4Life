package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
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
import androidx.test.core.app.ApplicationProvider;
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
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.intent.Intents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static org.hamcrest.Matchers.not;

import static java.lang.Thread.sleep;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapDisplayAndroidTestEspresso {
    @Rule public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    private Activity getCurrentActivity() {
        final Activity[] a = new Activity[1];
        activityScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    private View decorView;
    private UiDevice device;

    private void login()
    {
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

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity ->
        {
            decorView = activity.getWindow().getDecorView();
        });
        final Instrumentation inst = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(inst);
    }

    @Test
    public void Test_MapLoad() {
        login();
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
    }

    @Test
    public void Test_Map5Beaches() {
        login();
        Pattern pattern = Pattern.compile("beach", Pattern.CASE_INSENSITIVE);
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiSelector s = new UiSelector();
        for(int i = 0; i < 5; i++) // found at least 5 beaches (markers)
        {
            UiObject beach_marker = device.findObject(s.descriptionContains("Google Map").childSelector(s.instance(i)));
            assert(beach_marker != null);
        }
    }

    @Test
    public void Test_BeachesClickable() {
        login();
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        int clicks = 0;
        int i = 0;
        while(true)
        {
            UiObject beach_marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(i)));
            if(beach_marker == null)
                continue;
            i++;
            try
            {
                beach_marker.click();
                clicks++;
                if(clicks >= 5)
                    break;
            }
            catch(Exception e)
            {
                assert(false);
            }
        }
    }

    @Test
    public void Try_GoodBeachSelection() throws InterruptedException
    {
        login();
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        onView(withId(R.id.editTextTextPostalAddress)).perform(typeText("USC"), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        sleep(3000);
        onView(withText("Address loaded!")).inRoot(withDecorView(Matchers.not(decorView)));
        Random rand = new Random();
        int click = rand.nextInt(5);
        UiObject beach_marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(click)));
        try{
            beach_marker.click();
        } catch (Exception e) {}
        onView(withId(R.id.beach_go)).perform(click());
    }

    @Test
    public void Try_NoBeachSelection() throws InterruptedException
    {
        login();
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        onView(withId(R.id.beach_go)).perform(click());
        // fails
        onView(withText("Please enter and send address, then choose beach first!")).inRoot(withDecorView(Matchers.not(decorView)));
        onView(withId(R.id.editTextTextPostalAddress)).perform(typeText("USC"), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        sleep(3000);
        onView(withId(R.id.beach_go)).perform(click());
        // fails again
        onView(withText("Please enter and send address, then choose beach first!")).inRoot(withDecorView(Matchers.not(decorView)));
    }

    @Test
    public void Try_NoAddress() throws InterruptedException
    {
        login();
        device.wait(Until.hasObject(By.desc("MAP READY")), 20000);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(1)));
        marker.waitForExists(5000);
        onView(withId(R.id.beach_go)).perform(click());
        // fails
        onView(withText("Please enter and send address, then choose beach first!")).inRoot(withDecorView(Matchers.not(decorView)));
        Random rand = new Random();
        int click = rand.nextInt(5);
        UiObject beach_marker = device.findObject(new UiSelector().descriptionContains("Google Map").childSelector(new UiSelector().instance(click)));
        try{
            beach_marker.click();
        } catch (Exception e) {}
        onView(withId(R.id.beach_go)).perform(click());
        // fails again
        onView(withText("Please enter and send address, then choose beach first!")).inRoot(withDecorView(Matchers.not(decorView)));
    }
}
