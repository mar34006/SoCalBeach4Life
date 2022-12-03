package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewGalleryActivity extends AppCompatActivity {

    // Initializing the ImageView
    ImageView rImage;
    String beach_name;
    String user;
    Boolean explicit_call = true;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        user = intent.getStringExtra("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gallery);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        DatabaseReference getImage = databaseReference.child("beaches").child(beach_name).child("reviews");

        LinearLayout layout = findViewById(R.id.layout);

        getImage.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot dataSnapshot)
                    {

                        if(explicit_call) {
                            explicit_call = false;
                            for (DataSnapshot get_review : dataSnapshot.getChildren()) {

                                if (get_review.child("Image URL").exists()){
                                    String link = get_review.child("Image URL").getValue().toString();
                                    if(!link.equals("")) {

                                        ImageView imageView = new ImageView(ViewGalleryActivity.this);
                                        imageView.setImageResource(R.mipmap.ic_launcher);
                                        Picasso.get().load(link).into(imageView);

                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 500);

                                        if(count == 0){params.setMargins(0, 30, 0, 10);}
                                        else{params.setMargins(0, 10, 0, 10);}
                                        imageView.setLayoutParams(params);

                                        layout.addView(imageView);
                                        count += 1;

                                    }
                                }
                            }

                            if(count == 0){
                                TextView tv = (TextView) findViewById(R.id.name);
                                tv.setText("No images!");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(ViewGalleryActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
                    }
                });

        DatabaseReference change = getImage.push();
        change.setValue("Change");
        change.removeValue();
        
    }

    public void onClickBack(View view){
        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();
    }
}