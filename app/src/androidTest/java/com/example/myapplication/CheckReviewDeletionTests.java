package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

public class CheckReviewDeletionTests {

    public static final String STRING_TO_BE_DISPLAYED_REVIEW1 = "ceuhpowjidnjhru$3ugg2";
    public static final String STRING_TO_BE_DISPLAYED_RATING1 = "4 stars";
    public static final String STRING_TO_BE_DISPLAYED_USER1 = "Anonymous user:";

    public static final String STRING_TO_BE_DISPLAYED_REVIEW2 = "uhwqoijdio38jfhu38h";
    public static final String STRING_TO_BE_DISPLAYED_RATING2 = "3 stars";
    public static final String STRING_TO_BE_DISPLAYED_USER2 = "m@r.com";

    Boolean pass1 = false;
    Boolean pass2 = false;

    static Intent read_intent;
    static {
        read_intent = new Intent(ApplicationProvider.getApplicationContext(), ReadReviewActivity.class);
        read_intent.putExtra("beach_name", "will rogers");
        read_intent.putExtra("user", "m@r.com");
    }

    @Rule
    public ActivityScenarioRule<ReadReviewActivity> readReviewScenarioRule
            = new ActivityScenarioRule<>(read_intent);

    private Activity getCurrentActivity() {
        final Activity[] a = new Activity[1];
        readReviewScenarioRule.getScenario().onActivity(activity -> {
            a[0] = activity;
        });
        return a[0];
    }

    @Test
    public void ReadReview() {

        TextView name_view = getCurrentActivity().findViewById(R.id.name);
        String name = name_view.getText().toString();
        while(name.equals("")){
            name_view = getCurrentActivity().findViewById(R.id.name);
            name = name_view.getText().toString();
        }

        RelativeLayout container = getCurrentActivity().findViewById(R.id.container);
        Integer i = 1;
        Integer size = container.getChildCount();
        while(i < size){

            final TextView user_view = (TextView) container.getChildAt(i);
            String username = user_view.getText().toString();
            i += 1;

            final TextView rating_view = (TextView) container.getChildAt(i);
            String rating = rating_view.getText().toString();
            i += 1;

            final TextView review_view = (TextView) container.getChildAt(i);
            String review = review_view.getText().toString();
            i += 1;

            if(username.equals(STRING_TO_BE_DISPLAYED_USER1) && rating.equals(STRING_TO_BE_DISPLAYED_RATING1) && review.equals(STRING_TO_BE_DISPLAYED_REVIEW1)){
                pass1 = true;
                break;
            }
        }

        // If pass1 is True, then the review still exists in the database.
        // Thus, the test should fail
        if(pass1){
            onView(withText("True")).check(matches(withText("False")));
        }
    }

    @Test
    public void ReadAnonymousReview() {

        TextView name_view = getCurrentActivity().findViewById(R.id.name);
        String name = name_view.getText().toString();
        while(name.equals("")){
            name_view = getCurrentActivity().findViewById(R.id.name);
            name = name_view.getText().toString();
        }

        RelativeLayout container = getCurrentActivity().findViewById(R.id.container);
        Integer i = 1;
        Integer size = container.getChildCount();
        while(i < size){

            final TextView user_view = (TextView) container.getChildAt(i);
            String username = user_view.getText().toString();
            i += 1;

            final TextView rating_view = (TextView) container.getChildAt(i);
            String rating = rating_view.getText().toString();
            i += 1;

            final TextView review_view = (TextView) container.getChildAt(i);
            String review = review_view.getText().toString();
            i += 1;

            if(username.equals(STRING_TO_BE_DISPLAYED_USER2) && rating.equals(STRING_TO_BE_DISPLAYED_RATING2) && review.equals(STRING_TO_BE_DISPLAYED_REVIEW2)){
                pass2 = true;
                break;
            }
        }

        // If pass1 is True, then the review still exists in the database.
        // Thus, the test should fail
        if(pass2){
            onView(withText("True")).check(matches(withText("False")));
        }
    }
}

