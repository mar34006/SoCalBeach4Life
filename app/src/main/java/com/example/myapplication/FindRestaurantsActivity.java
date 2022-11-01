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

import java.io.Serializable;
import java.util.ArrayList;

class Restaurant implements Serializable
{
    String name;
    double coords[] = new double[2];
    String menu_url;
    double distance;
}

class RestaurantPacks implements Serializable
{
    Restaurant restaurants[];
    int distance;
}

public class FindRestaurantsActivity extends AppCompatActivity {


    FirebaseDatabase root;
    DatabaseReference reference;
    String beach_name;
    ArrayList<Restaurant> restaurants = new ArrayList<>();
    Context context = this;

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

        // restaurants

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("restaurants");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot get_restaurant : dataSnapshot.getChildren()) {
                    Restaurant restaurant = new Restaurant();
                    restaurant.name = get_restaurant.child("name").getValue(String.class);
                    restaurant.coords[0] = get_restaurant.child("coords").child("lat").getValue(double.class);
                    restaurant.coords[1] = get_restaurant.child("coords").child("long").getValue(double.class);
                    restaurant.menu_url = get_restaurant.child("menu").getValue(String.class);
                    restaurant.distance = get_restaurant.child("distance").getValue(double.class);
                    restaurants.add(restaurant);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                String issue = ("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });
    }

    public void onClickD1(View view){send_restaurants(1000);}
    public void onClickD2(View view){send_restaurants(2000);}
    public void onClickD3(View view){send_restaurants(2000);}

    public void send_restaurants(Integer dist){

        ArrayList<Restaurant> valid_restaurants = new ArrayList<>();
        for(Restaurant restaurant: restaurants){
            if (restaurant.distance <= dist){
                valid_restaurants.add(restaurant);
            }
        }

        // --GERARDO--
        Intent intent = new Intent(context, MapsActivity.class);
        RestaurantPacks p = new RestaurantPacks();
        Restaurant r[] = new Restaurant[valid_restaurants.size()];
        for(int i =  0; i < valid_restaurants.size(); i++)
        {
            r[i] = valid_restaurants.get(i);
        }
        p.restaurants = r;
        p.distance = dist;
        intent.putExtra("mode", "restaurants");
        intent.putExtra("name", beach_name);
        intent.putExtra("restaurants", p);
        startActivity(intent);

        this.finish();

    }
}