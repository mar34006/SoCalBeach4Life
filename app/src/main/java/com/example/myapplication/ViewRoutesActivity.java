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


public class ViewRoutesActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;

    String user;
    Boolean forceDataChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_routes);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        RelativeLayout containerLayout = (RelativeLayout) findViewById(R.id.container);

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("Users");
        Context context = this;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(forceDataChange) {
                    for (DataSnapshot check_user : dataSnapshot.getChildren()) {
                        if (check_user.child("email").getValue().equals(user)) {

                            check_user = check_user.child("routes");
                            if(dataSnapshot.exists()) {

                                TextView title = findViewById(R.id.title);
                                title.setText("Saved routes");

                                TextView route_title = findViewById(R.id.route_title);
                                route_title.setText("Your saved routes");

                                int i = 0;
                                for (DataSnapshot route : check_user.getChildren()){
                                    String start = route.child("Start").getValue().toString();
                                    String destination = route.child("Destination").getValue().toString();
                                    String time = route.child("Time").getValue().toString();

                                    TextView startText = new TextView(context);
                                    startText.setText(start);
                                    startText.setTextSize(20);
                                    startText.setPadding(0, (i * 30), 0, 0);
                                    containerLayout.addView(startText);
                                    i += 3;

                                    TextView destinationText = new TextView(context);
                                    destinationText.setText(destination);
                                    destinationText.setTextSize(20);
                                    destinationText.setPadding(0, (i * 30), 0, 0);
                                    containerLayout.addView(destinationText);
                                    i += 3;

                                    TextView timeText = new TextView(context);
                                    timeText.setText(time);
                                    timeText.setTextSize(20);
                                    timeText.setPadding(0, (i * 30), 0, 0);
                                    containerLayout.addView(timeText);
                                    i += 3;

                                    i += 3;
                                }
                            }
                            else{
                                TextView title = findViewById(R.id.title);
                                title.setText("No routes saved!");
                            }
                            break;
                        }
                    }
                    forceDataChange = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });
        DatabaseReference change = reference.push();
        change.setValue("Change");
        change.removeValue();
    }

    public void onClickBack(View view){ this.finish(); }

}