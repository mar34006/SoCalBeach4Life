package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextAddress, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView)findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button)findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFirstName = (EditText)findViewById(R.id.firstName);
        editTextLastName = (EditText)findViewById(R.id.lastName);
        editTextEmail = (EditText)findViewById(R.id.email);
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPassword = (EditText)findViewById(R.id.password);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;

        }

    }

    public void onClickBack(View view){
        this.finish();
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String address = editTextAddress.getText().toString();

        // Validating all the input strings
        if(firstName.isEmpty()) {
            editTextFirstName.setError("First name is required.");
            editTextFirstName.requestFocus();
            return;
        }

        if(lastName.isEmpty()) {
            editTextLastName.setError("Last name is required.");
            editTextLastName.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Password is required.");
            editTextPassword.requestFocus();
            return;
        }

        if(address.isEmpty()) {
            editTextPassword.setError("Address is required.");
            editTextPassword.requestFocus();
            return;
        }

        // Check if the email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email.");
            editTextEmail.requestFocus();
            return;
        }

        // Password should be greater than 6 characters
        if(password.length() < 6) {
            editTextPassword.setError("Password should contain a minimum of 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        // If complete, we show the progress bar
        // and we create user object and send to Firebase
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    UserInterface user = new UserInterface(firstName, lastName, email, address);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this, R.string.register_successful_toast, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterUser.this, LoginActivity.class));
                                    }
                                    else {
                                        Toast.makeText(RegisterUser.this, R.string.register_failure_toast, Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterUser.this, R.string.register_failure_toast, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}