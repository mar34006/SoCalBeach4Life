package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button logout;
    private Button update;

    String firstName, lastName, email, address;
    TextView firstNameTextView, lastNameTextView, emailTextView, addressTextView, banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        update = (Button) findViewById(R.id.updateInfo);

        // Clicking on the banner will redirect back to the BeachActivity page
        banner = (TextView)findViewById(R.id.banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When user clicks on logout, it redirects them to the main activity
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        logout = (Button) findViewById(R.id.signOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When user clicks on logout, it redirects them to the main activity
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        // Displaying user information in profile page
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        firstNameTextView = (TextView) findViewById(R.id.firstName);
        lastNameTextView = (TextView) findViewById(R.id.lastName);
        emailTextView = (TextView) findViewById(R.id.email);
        addressTextView = (TextView)findViewById(R.id.address);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInterface userProfile = snapshot.getValue(UserInterface.class);
                if (userProfile != null) {
                    // Retrieving our values from instance of User class
                    firstName = userProfile.getFirstName();
                    lastName = userProfile.getLastName();
                    email = userProfile.getEmail();
                    address = userProfile.getAddress();

                    // Setting the TextViews to appropriate text
                    firstNameTextView.setText(firstName);
                    lastNameTextView.setText(lastName);
                    emailTextView.setText(email);
                    addressTextView.setText(address);

                }
            }

            // If something went wrong, make a toast
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void update(View view) {
        if(isFirstNameChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show();
        }
        if(isLastNameChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show();
        }
        if(isAddressChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickViewRoutes(View view){
        Intent intent = new Intent(this, ViewRoutesActivity.class);
        intent.putExtra("user", email);
        startActivity(intent);
    }

    public void onClickBack(View view){ this.finish(); }

    private boolean isFirstNameChanged() {
        if(!firstName.equals(firstNameTextView.getText().toString().trim())) {
            reference.child(userID).child("firstName").setValue(firstNameTextView.getText().toString().trim());
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isLastNameChanged() {
        if(!lastName.equals(lastNameTextView.getText().toString().trim())) {
            reference.child(userID).child("lastName").setValue(lastNameTextView.getText().toString().trim());
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if(!address.equals(addressTextView.getText().toString().trim())) {
            reference.child(userID).child("address").setValue(addressTextView.getText().toString().trim());
            return true;
        }
        else {
            return false;
        }
    }

}