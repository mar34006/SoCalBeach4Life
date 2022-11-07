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
    Boolean forceDataChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        restaurant_name = intent.getStringExtra("restaurant_name");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name).child("restaurants");

        TextView rest_view_name = findViewById(R.id.restaurant_title);
        TextView rest_view_hours = findViewById(R.id.hours);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(forceDataChange) {
                    for(DataSnapshot rest_snap: dataSnapshot.getChildren()) {
                        if(rest_snap.child("name").equals(restaurant_name)) {
                            String name = rest_snap.child("name").getValue(String.class);
                            rest_view_name.setText(name);

                            String hours = rest_snap.child("hours").getValue(String.class);
                            rest_view_hours.setText(hours);

                            menu = rest_snap.child("menu").getValue(String.class);

                            forceDataChange = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                rest_view_name.setText("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference change = reference.push();
        change.setValue("Change");
        change.removeValue();

    }

    public void onClickMenu(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menu));
        startActivity(browserIntent);
    }

    public void onClickBack(View view){
        this.finish();
    }
}