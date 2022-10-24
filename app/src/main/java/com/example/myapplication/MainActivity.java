package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;

    String beach_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = getIntent();
        // beach_name = intent.getStringExtra("beach_name");
        beach_name = "will rogers";

        // Name

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("name");

        TextView name = findViewById(R.id.beach_title);

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


        // Description

        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("description");

        TextView description = findViewById(R.id.description);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                description.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                description.setText("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });
    }

    public void onClickRouteToBeach(View view){

        final String[] lot1 = new String[1];
        final String[] lot2 = new String[1];

        reference = root.getReference("beaches");
        reference = reference.child(beach_name).child("lots");
        reference = reference.child("lot1");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                lot1[0] = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                lot1[0] = "Error in retrieving your longitude!";
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });

        reference = root.getReference("beaches");
        reference = reference.child(beach_name).child("lots");
        reference = reference.child("lot2");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                lot2[0] = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                lot2[0] = "Error in retrieving your longitude!";
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });

        //Intent intent = new Intent(this, MapActivity.class);
        //intent.putExtra("lot1", lot1[0]);
        //intent.putExtra("lot2", lot2[0]);
        //startActivity(intent);
        //this.finish();

    }

    public void onClickReadReview(View view){
        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        startActivity(intent);
    }

    public void onClickFindRestaurant(View view){
        Intent intent = new Intent(this, FindRestaurantsActivity.class);
        intent.putExtra("beach_name", beach_name);
        startActivity(intent);
        this.finish();
    }
}