package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class Beach{
    String name;
    double[] loc = new double[2];
}

public class DisplayBeaches extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;
    ArrayList<Beach> beaches = new ArrayList<>();
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_beaches);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot get_beach : dataSnapshot.getChildren()) {

                    Beach beach = new Beach();

                    beach.name = get_beach.child("name").getValue().toString();

                    beach.loc[0] = get_beach.child("lat").getValue(double.class);
                    beach.loc[1] = get_beach.child("long").getValue(double.class);

                    beaches.add(beach);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read values.", error.toException());
            }
        });
    }
}