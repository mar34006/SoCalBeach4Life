package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import java.util.concurrent.TimeUnit;

public class LeaveReviewActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference reference;

    FirebaseStorage storage;
    StorageReference storageRef;

    DatabaseReference async_review;

    String beach_name;
    String user;
    EditText inputText;
    int rating = 1;
    Boolean anonymous = true;
    Uri image_uri;
    String image_url = "";
    Boolean wait = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        Intent intent = getIntent();
        beach_name = intent.getStringExtra("beach_name");
        user = intent.getStringExtra("user");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("name");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        TextView name = findViewById(R.id.name);

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



        inputText = (EditText) findViewById(R.id.inputText);

    }


    public void onClickMinus(View view){
        if (rating != 1){
            rating -= 1;
            TextView rating_view = findViewById(R.id.rating);
            rating_view.setText(Integer.toString(rating));
        }
    }

    public void onClickPlus(View view){
        if (rating != 5){
            rating += 1;
            TextView rating_view = findViewById(R.id.rating);
            rating_view.setText(Integer.toString(rating));
        }
    }

    public void onClickAnon(View view){
        TextView tv = (TextView) view;
        if(anonymous){
            anonymous = false;
            tv.setText("No");
        }
        else{
            anonymous = true;
            tv.setText("Yes");
        }
    }

    public void onClickLeaveRev(View view) {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("beaches");
        reference = reference.child(beach_name);
        reference = reference.child("reviews");


        DatabaseReference review = reference.push();
        async_review = review;

        review.child("Rating").setValue(rating);
        if (inputText == null){review.child("Review").setValue("");}
        else{ review.child("Review").setValue(inputText.getText().toString());}
        review.child("Anonymous").setValue(anonymous);
        review.child("User").setValue(user);

        if(image_uri != null){ uploadImage(); }
        else{ review.child("Image URL").setValue("");}

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();

    }

    public void onClickImage(View view){
        Intent image_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(image_intent, 1738);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            image_uri = data.getData();
            TextView tv = (TextView) findViewById(R.id.upload_image);
            tv.setText("Image uploaded!");
        }
    }

    public void onClickBack(View view){
        Intent intent = new Intent(this, ReadReviewActivity.class);
        intent.putExtra("beach_name", beach_name);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();
    }

    public void uploadImage() {
        String name = UUID.randomUUID().toString();
        StorageReference image_ref = storageRef.child(beach_name + "/" + name);

        image_ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                image_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        image_url = downloadUrl.toString();
                        async_review.child("Image URL").setValue(image_url);
                    }
                });
            }
        });
    }
}