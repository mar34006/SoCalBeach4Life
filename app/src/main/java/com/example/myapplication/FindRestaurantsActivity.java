package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import java.util.ArrayList;

public class FindRestaurantsActivity extends AppCompatActivity {


    FirebaseDatabase root;
    DatabaseReference reference;
    String beach_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_restaurants);

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");

        TextView name = findViewById(R.id.name);

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name).child("name");
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
    }

    public void onClickD1(View view){send_restaurants(1000);}
    public void onClickD2(View view){send_restaurants(2000);}
    public void onClickD3(View view){send_restaurants(2000);}

    public void send_restaurants(Integer dist){

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("restaurants");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> keys = new ArrayList<String>();
                for (DataSnapshot get_restaurant : dataSnapshot.getChildren()) {
                    String distance = get_restaurant.child("distance").getValue().toString();
                    if (Integer.parseInt(distance) <= dist) {
                        keys.add(get_restaurant.getKey());
                    }
                }

                //Intent intent = new Intent(context, MapActivity.class);
                //intent.putExtra("beach_name", beach_name);
                //intent.putExtra("distance", dist);
                //intent.putExtra("keys", keys);
                //startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                String issue = ("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });

        this.finish();

    }
}