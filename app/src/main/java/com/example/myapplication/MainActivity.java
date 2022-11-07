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


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;

    String beach_name;
    String actual_beach_name = null;
    String user;
    final double[] lot1 = new double[2];
    final double[] lot2 = new double[2];
    double[] loc = new double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        user = intent.getStringExtra("user");

        // Name

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);

        TextView view_name = findViewById(R.id.beach_title);
        TextView view_description = findViewById(R.id.description);
        TextView view_hours = findViewById(R.id.hours);
        TextView view_location = findViewById(R.id.location);

        Button toProfileBtn = (Button)findViewById(R.id.profilePage);

        toProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                actual_beach_name = name;
                view_name.setText(name);

                String description = dataSnapshot.child("description").getValue(String.class);
                view_description.setText(description);

                String hours = dataSnapshot.child("hours").getValue(String.class);
                view_hours.setText(hours);

                String location = dataSnapshot.child("location").getValue(String.class);
                view_location.setText("Location: \n" + location);

                lot1[0] = dataSnapshot.child("lots").child("lot1").child("lat").getValue(double.class);
                lot1[1] = dataSnapshot.child("lots").child("lot1").child("long").getValue(double.class);

                lot2[0] = dataSnapshot.child("lots").child("lot2").child("lat").getValue(double.class);
                lot2[1] = dataSnapshot.child("lots").child("lot2").child("long").getValue(double.class);

                loc[0] = dataSnapshot.child("lat").getValue(Double.class);
                loc[1] = dataSnapshot.child("long").getValue(Double.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                view_name.setText("Error in retrieving your message!");
                Log.w("SecondFragment", "Failed to read value.", error.toException());
            }
        });
    }

    public void onClickRouteToBeach(View view){

        // -- GERARDO --

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("mode", "beach lots");
        intent.putExtra("name", beach_name);
        intent.putExtra("lot1", lot1);
        intent.putExtra("lot2", lot2);
        intent.putExtra("user", user);
        intent.putExtra("a_beach_name", actual_beach_name);
        Log.i("LOG DATA", String.format("lot1: (%f, %f)", lot1[0], lot1[1]));
        Log.i("LOG DATA", String.format("lot2: (%f, %f)", lot2[0], lot2[1]));
        intent.putExtra("loc", loc);
        startActivity(intent);
        //this.finish();

    }

    public void onClickReadReview(View view){
        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onClickFindRestaurant(View view){
        Intent intent = new Intent(this, FindRestaurantsActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("a_beach_name", actual_beach_name);
        intent.putExtra("loc", loc);
        startActivity(intent);
        //this.finish();
    }

    public void onClickBack(View view){
        this.finish();
    }

}

/*

---GERARDO---

class Beach{
    String name;
    double[] lot1 = new double[2];
    double[] lot2 = new double[2];
}

INCLUDE THIS IN YOUR onCreate FUNCTION TO CREATE A LIST OF ALL BEACH NAMES + LOCATIONS

root = FirebaseDatabase.getInstance();
reference = root.getReference("beaches");

Context context = this;
ArrayList<Beach> beaches = new ArrayList<>();

reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot get_beach : dataSnapshot.getChildren()) {

            Beach beach = new Beach();

            beach.name = get_review.child("name").getValue().toString();

            beach.lot1[0] = get_review.child("lots").child("lot1").child("lat").getValue(double.class);
            beach.lot1[1] = get_review.child("lots").child("lot1").child("long").getValue(double.class);

            beach.lot2[0] = get_review.child("lots").child("lot2").child("lat").getValue(double.class);
            beach.lot2[1] = get_review.child("lots").child("lot2").child("long").getValue(double.class);

            beaches.add(beach);
        }
    }
    @Override
    public void onCancelled(DatabaseError error) {
        Log.w("Failed to read values.", error.toException());
    }
});

 */