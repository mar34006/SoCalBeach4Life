package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReadReviewActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;
    String beach_name;
    String user;
    Boolean explicit_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_review);

        explicit_call = true;

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        user = intent.getStringExtra("user");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches").child(beach_name).child("name");

        TextView name = findViewById(R.id.name);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                name.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                name.setText("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });


        RelativeLayout containerLayout = (RelativeLayout) findViewById(R.id.container);

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches").child(beach_name).child("reviews");

        Context context = this;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = 0;
                int count = 0;
                double total_rating = 0;
                if(explicit_call) {
                    explicit_call = false;
                    for (DataSnapshot get_review : dataSnapshot.getChildren()) {

                        String rating = get_review.child("Rating").getValue().toString();
                        String text_review = get_review.child("Review").getValue().toString();
                        String anonymous = get_review.child("Anonymous").getValue().toString();
                        String username = get_review.child("User").getValue().toString();

                        TextView userText = new TextView(context);
                        if (Boolean.valueOf(anonymous)) {
                            userText.setText("Anonymous user:");
                        } else {
                            userText.setText(username);
                        }
                        userText.setTextSize(20);
                        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                        userText.setTypeface(boldTypeface);
                        userText.setPadding(0, (i * 30), 0, 0);
                        containerLayout.addView(userText);
                        i += 3;

                        TextView ratingText = new TextView(context);
                        total_rating += Integer.parseInt(rating);
                        ratingText.setText(rating + " stars");
                        ratingText.setTextSize(20);
                        ratingText.setPadding(0, (i * 30), 0, 0);
                        containerLayout.addView(ratingText);
                        i += 3;

                        TextView reviewText = new TextView(context);
                        reviewText.setText(text_review);
                        reviewText.setTextSize(20);
                        reviewText.setPadding(0, (i * 30), 0, 0);
                        containerLayout.addView(reviewText);
                        i += 3;

                        count += 1;
                        i += 3;

                    }

                    AverageCalculator calc = new AverageCalculator();
                    String display = calc.calculate(total_rating, count);

                    TextView view_avg_rating = findViewById(R.id.average_reviews);
                    view_avg_rating.setText(display);

                    TextView view_num_reviews = findViewById(R.id.num_reviews);
                    view_num_reviews.setText("Number of reviews: " + count);

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                name.setText("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });


    }
    public void onClickBack(View view){
        this.finish();
    }

    public void onClickLeaveReview(View view){
        Intent intent = new Intent(this, LeaveReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish(); 
    }

    public void onClickDeleteReview(View view){
        Intent intent = new Intent(this, DeleteReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();
    }
}