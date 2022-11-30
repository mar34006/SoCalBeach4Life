package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseDatabase root;
    DatabaseReference reference;

    FirebaseStorage storage;
    StorageReference storageRef;

    String user;
    String beach_name;
    Boolean forceDataChange = true;
    ArrayList<String> delete_reviews = new ArrayList<>();
    HashMap<String, DataSnapshot> review_keys = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_review);


        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        RelativeLayout containerLayout = (RelativeLayout) findViewById(R.id.container);
        Context context = this;

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        beach_name = intent.getStringExtra("beach_name");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(forceDataChange) {
                    int i = 0;
                    int count = 0;
                    for (DataSnapshot check_beach : dataSnapshot.getChildren()) {
                         if(check_beach.getKey().equals(beach_name)){
                             DataSnapshot review_snap = check_beach.child("reviews");
                             for (DataSnapshot get_review : review_snap.getChildren()) {
                                 if (get_review.child("User").getValue().equals(user)) {

                                     TextView label = new TextView(context);
                                     label.setText("Review: " + (count + 1));
                                     delete_reviews.add("Review: " + (count + 1));
                                     review_keys.put("Review: " + (count + 1), get_review);
                                     label.setTextSize(20);
                                     Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                                     label.setTypeface(boldTypeface);
                                     label.setPadding(0, (i * 30), 0, 0);
                                     containerLayout.addView(label);

                                     i += 3;

                                     String rating = get_review.child("Rating").getValue().toString();
                                     String text_review = get_review.child("Review").getValue().toString();
                                     String anonymous = get_review.child("Anonymous").getValue().toString();

                                     String image = "";
                                     if(get_review.child("Image URL").exists()) { image = get_review.child("Image URL").getValue().toString();}

                                     TextView ratingText = new TextView(context);
                                     ratingText.setText(rating + " stars");
                                     ratingText.setTextSize(20);
                                     ratingText.setPadding(0, (i * 30), 0, 0);
                                     containerLayout.addView(ratingText);
                                     i += 3;

                                     TextView reviewText = new TextView(context);
                                     if(text_review.equals("")){ text_review = "None"; }
                                     reviewText.setText("Review: " + text_review);
                                     reviewText.setTextSize(20);
                                     reviewText.setPadding(0, (i * 30), 0, 0);
                                     containerLayout.addView(reviewText);
                                     i += 3;


                                     TextView anonymousText = new TextView(context);
                                     anonymousText.setText("Anonymous review: " + anonymous.toString());
                                     anonymousText.setTextSize(20);
                                     anonymousText.setPadding(0, (i * 30), 0, 0);
                                     containerLayout.addView(anonymousText);

                                     i += 3;

                                     TextView imageText = new TextView(context);
                                     if(image.equals("")){ image = "False"; }
                                     else{ image = "True"; }
                                     imageText.setText("Image uploaded: " + image);
                                     imageText.setTextSize(20);
                                     imageText.setPadding(0, (i * 30), 0, 0);
                                     containerLayout.addView(imageText);

                                     i += 3;

                                     i += 3;
                                     count += 1;

                                 }
                             }
                         }
                    }

                    if(i == 0){
                        TextView tv = findViewById(R.id.review_title);
                        tv.setText("You have not left any reviews!");
                    }

                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, delete_reviews);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(DeleteReviewActivity.this,  delete_reviews.get(position), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

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

        Button button = findViewById(R.id.delete_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delete_reviews.size() != 0) {
                    String text = String.valueOf(spinner.getSelectedItem());
                    DataSnapshot key = review_keys.get(text);

                    String image_url = key.child("Image URL").getValue().toString();
                    if(!image_url.equals("")) {
                        storage = FirebaseStorage.getInstance();
                        storageRef = storage.getReferenceFromUrl(image_url);
                        storageRef.delete();
                    }

                    key.getRef().removeValue();
                    View backView = findViewById(R.id.back);
                    onClickBack(backView);
                }
            }
        });
    }

    public void onClickBack(View view){
        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = delete_reviews.get(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}