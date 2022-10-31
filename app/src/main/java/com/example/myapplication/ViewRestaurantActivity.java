package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewRestaurantActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;

    String beach_name;
    String restaurant_name;
    String menu;
    double coords[] = new double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        restaurant_name = intent.getStringExtra("restaurant_name");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name).child("restaurants").child(restaurant_name);

        TextView rest_view_name = findViewById(R.id.restaurant_title);
        TextView rest_view_description = findViewById(R.id.restaurant_description);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue(String.class);
                rest_view_name.setText(name);

                menu = dataSnapshot.child("menu").getValue(String.class);

                coords[0] = dataSnapshot.child("coords").child("lat").getValue(double.class);
                coords[1] = dataSnapshot.child("coords").child("long").getValue(double.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                rest_view_name.setText("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });

    }

    public void onClickRoute(View view){
        // GERARDO
        //Intent intent = new Intent(this, MapActivity.class);
        //intent.putExtra("name", restaurant_name);
        //intent.putExtra("lat", coords[0]);
        //intent.putExtra("long", coords[1]);
        //startActivity(intent);
        this.finish();
    }

    public void onClickMenu(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menu));
        startActivity(browserIntent);
    }

    public void onClickBack(View view){
        this.finish();
    }
}