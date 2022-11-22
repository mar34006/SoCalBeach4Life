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


/**
 * Basic tests showcasing simple view matchers and actions like {@link ViewMatchers#withId},
 * {@link ViewActions#click} and {@link ViewActions#typeText}.
 * <p>
 * Note that there is no need to tell Espresso that a view is in a different {@link Activity}.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LeaveReviewTest {

    public static final String STRING_TO_BE_TYPED_REVIEW1 = "ceuhpowjidnjhru$3ugg2";
    public static final String STRING_TO_BE_TYPED_REVIEW2 = "uhwqoijdio38jfhu38h";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes.
     */


    static Intent leave_intent;
    static {
        leave_intent = new Intent(ApplicationProvider.getApplicationContext(), LeaveReviewActivity.class);
        leave_intent.putExtra("beach_name", "will rogers");
        leave_intent.putExtra("user", "m@r.com");
    }
    @Rule
    public ActivityScenarioRule<LeaveReviewActivity> leaveReviewScenarioRule
            = new ActivityScenarioRule<>(leave_intent);

    @Test
    public void LeaveAnonymousReview() {

        onView(withId(R.id.inputText))
                .perform(typeText(STRING_TO_BE_TYPED_REVIEW1), closeSoftKeyboard());

        onView(withId(R.id.plus)).perform(click());
        onView(withId(R.id.plus)).perform(click());
        onView(withId(R.id.plus)).perform(click());

        onView(withId(R.id.leave_rev)).perform(click());
    }

    @Test
    public void LeaveReview() {

        onView(withId(R.id.inputText))
                .perform(typeText(STRING_TO_BE_TYPED_REVIEW2), closeSoftKeyboard());

        onView(withId(R.id.plus)).perform(click());
        onView(withId(R.id.plus)).perform(click());

        onView(withId(R.id.anon)).perform(click());

        onView(withId(R.id.leave_rev)).perform(click());
    }
}

