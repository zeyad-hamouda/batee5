package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;



public class EmailVerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mVerifyButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String dob = getIntent().getStringExtra("dob");
        String displayName = getIntent().getStringExtra("displayName");
        boolean isSellerAccount = getIntent().getBooleanExtra("isSellerAccount", false);

        mVerifyButton = findViewById(R.id.verify_button);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(EmailVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(EmailVerificationActivity.this, "Registration successful! Please check your email for verification.", Toast.LENGTH_LONG).show();

                                                            String userId = user.getUid();
                                                            User newUser = new User(email, phoneNumber, password, firstName, lastName, dob, displayName, isSellerAccount);                                                            mDatabase.child(userId).setValue(newUser);

                                                            Intent intent = new Intent(EmailVerificationActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(EmailVerificationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(EmailVerificationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

