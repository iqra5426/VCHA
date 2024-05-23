package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonAction;
    private TextView textViewAction;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonAction = findViewById(R.id.buttonAction);
        textViewAction = findViewById(R.id.textViewAction);

        // Initially hide confirm password field
        editTextPassword.setVisibility(View.VISIBLE);

        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionText = textViewAction.getText().toString();
                if (actionText.contains("Register")) {
                    // Registration logic
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                } else {
                    // Login logic
                    loginUser();
                }
            }
        });

        textViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start RegisterActivity when "Register here" is clicked
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        toggleAction();
    }

    // Method to toggle between Login and Register actions
    private void toggleAction() {
        String actionText = textViewAction.getText().toString();
        if (actionText.contains("Register")) {
            // Change to Login action
            editTextPassword.setVisibility(View.VISIBLE);
            buttonAction.setText("Login");
            textViewAction.setText("Already have an account? Login here");
        } else {
            // Change to Register action
            editTextPassword.setVisibility(View.GONE);
            buttonAction.setText("Register");
            textViewAction.setText("Don't have an account? Register here");
        }
    }

    // Method to login an existing user using Firebase Authentication
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in user with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Close the login activity
                        } else {
                            // Login failed
                            Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}