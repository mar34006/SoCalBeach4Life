package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LeaveReviewActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;
    String beach_name;
    String user;
    EditText inputText;
    int rating = 1;
    Boolean anonymous = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        user = intent.getStringExtra("user");

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



        inputText = (EditText) findViewById(R.id.inputText);

    }


    public void onClickMinus(View view){
        if (rating != 1){
            rating -= 1;
            TextView rating_view = findViewById(R.id.rating);
            rating_view.setText(Integer.toString(rating));
        }
    }

    public void onClickPlus(View view){
        if (rating != 5){
            rating += 1;
            TextView rating_view = findViewById(R.id.rating);
            rating_view.setText(Integer.toString(rating));
        }
    }

    public void onClickAnon(View view){
        TextView tv = (TextView) view;
        if(anonymous){
            anonymous = false;
            tv.setText("No");
        }
        else{
            anonymous = true;
            tv.setText("Yes");
        }
    }

    public void onClickLeaveRev(View view) {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("reviews");

        DatabaseReference review = reference.push();
        review.child("Rating").setValue(rating);
        if (inputText == null){review.child("Review").setValue("");}
        else{ review.child("Review").setValue(inputText.getText().toString());}
        review.child("Anonymous").setValue(anonymous);
        review.child("User").setValue(user);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();

    }

    public void onClickBack(View view){
        this.finish();
    }
}