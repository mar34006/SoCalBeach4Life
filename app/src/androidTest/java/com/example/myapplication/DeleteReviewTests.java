package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasToString;
import static java.util.EnumSet.allOf;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.database.DataSnapshot;

import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

public class DeleteReviewTests {

    public static final String STRING_TO_BE_DISPLAYED_REVIEW1 = "ceuhpowjidnjhru$3ugg2";
    public static final String STRING_TO_BE_DISPLAYED_RATING1 = "4 stars";
    public static final String STRING_TO_BE_DISPLAYED_ANONYMOUS1 = "Anonymous review: true";
    String selection1 = "";

    public static final String STRING_TO_BE_DISPLAYED_REVIEW2 = "uhwqoijdio38jfhu38h";
    public static final String STRING_TO_BE_DISPLAYED_RATING2 = "3 stars";
    public static final String STRING_TO_BE_DISPLAYED_ANONYMOUS2 = "Anonymous review: false";
    String selection2 = "";

    static Intent delete_intent;
    static {
        delete_intent = new Intent(ApplicationProvider.getApplicationContext(), DeleteReviewActivity.class);
        delete_intent.putExtra("beach_name", "will rogers");
        delete_intent.putExtra("user", "m@r.com");
    }

    @Rule
    public ActivityScenarioRule<DeleteReviewActivity> deleteReviewScenarioRule
            = new ActivityScenarioRule<>(delete_intent);

    private Activity getCurrentActivity() {
        final Activity[] a = new Activity[1];
        deleteReviewScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    @Test
    public void DeleteAnonymousReview() {

        RelativeLayout container = getCurrentActivity().findViewById(R.id.container);
        Integer size = container.getChildCount();

        long then = System.currentTimeMillis();
        then += 6000;
        while(size == 1){
            container = getCurrentActivity().findViewById(R.id.container);
            size = container.getChildCount();
            long now = System.currentTimeMillis();
            if(now > then){
                break;
            }
        }

        Integer i = 1;

        while(i < size){

            final TextView label_view = (TextView) container.getChildAt(i);
            String label = label_view.getText().toString();
            i += 1;

            final TextView rating_view = (TextView) container.getChildAt(i);
            String rating = rating_view.getText().toString();
            i += 1;

            final TextView review_view = (TextView) container.getChildAt(i);
            String review = review_view.getText().toString();
            i += 1;

            final TextView anonymous_view = (TextView) container.getChildAt(i);
            String anonymous = anonymous_view.getText().toString();
            i += 1;

            if(anonymous.equals(STRING_TO_BE_DISPLAYED_ANONYMOUS1) && rating.equals(STRING_TO_BE_DISPLAYED_RATING1) && review.equals(STRING_TO_BE_DISPLAYED_REVIEW1)){
                selection1 = label;
                break;
            }
        }

        // If selection1 is not empty, we were able to find the review to delete
        // If we reach the end of the if-block, we are able to delete the review
        if(!selection1.equals("")){
            onView(withId(R.id.spinner)).perform(click());
            onData(hasToString(selection1)).perform(click());
            onView(withId(R.id.delete_button)).perform(click());
        }

    }

    @Test
    public void DeleteReview() {

        RelativeLayout container = getCurrentActivity().findViewById(R.id.container);
        Integer size = container.getChildCount();

        long then = System.currentTimeMillis();
        then += 6000;
        while(size == 1){
            container = getCurrentActivity().findViewById(R.id.container);
            size = container.getChildCount();
            long now = System.currentTimeMillis();
            if(now > then){
                break;
            }
        }

        Integer i = 1;

        while(i < size){

            final TextView label_view = (TextView) container.getChildAt(i);
            String label = label_view.getText().toString();
            i += 1;

            final TextView rating_view = (TextView) container.getChildAt(i);
            String rating = rating_view.getText().toString();
            i += 1;

            final TextView review_view = (TextView) container.getChildAt(i);
            String review = review_view.getText().toString();
            i += 1;

            final TextView anonymous_view = (TextView) container.getChildAt(i);
            String anonymous = anonymous_view.getText().toString();
            i += 1;

            if(anonymous.equals(STRING_TO_BE_DISPLAYED_ANONYMOUS2) && rating.equals(STRING_TO_BE_DISPLAYED_RATING2) && review.equals(STRING_TO_BE_DISPLAYED_REVIEW2)){
                selection2 = label;
                break;
            }
        }

        // If selection1 is not empty, we were able to find the review to delete
        // If we reach the end of the if-block, we are able to delete the review
        if(!selection2.equals("")) {
            onView(withId(R.id.spinner)).perform(click());
            onData(hasToString(selection2)).perform(click());
            onView(withId(R.id.delete_button)).perform(click());
        }
    }
}
