package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

public class ReadReviewActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;
    String beach_name;
    Boolean explicit_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_review);

        explicit_call = true;

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("name");

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
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("reviews");

        Context context = this;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                if(explicit_call) {
                    explicit_call = false;
                    for (DataSnapshot get_review : dataSnapshot.getChildren()) {

                        String rating = get_review.child("Rating").getValue().toString();
                        String text_review = get_review.child("Review").getValue().toString();
                        String anonymous = get_review.child("Anonymous").getValue().toString();

                        TextView userText = new TextView(context);
                        if (Boolean.valueOf(anonymous)) {
                            userText.setText("Anonymous user:");
                        } else {
                            userText.setText("User 1:");
                        }
                        userText.setTextSize(20);
                        userText.setPadding(0, (i * 30), 0, 0);
                        containerLayout.addView(userText);
                        i += 3;

                        TextView ratingText = new TextView(context);
                        ratingText.setText(rating + " stars");
                        ratingText.setTextSize(20);
                        ratingText.setPadding(0, (i * 30), 0, 0);
                        containerLayout.addView(ratingText);
                        i += 3;

                        if (text_review != "") {
                            TextView reviewText = new TextView(context);
                            reviewText.setText(text_review);
                            reviewText.setTextSize(20);
                            reviewText.setPadding(0, (i * 30), 0, 0);
                            containerLayout.addView(reviewText);
                            i += 3;
                        }

                        i += 3;

                    }
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
        startActivity(intent);
        this.finish(); 
    }
}