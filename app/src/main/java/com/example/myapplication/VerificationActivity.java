package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mResendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        mResendButton = findViewById(R.id.resend_button);

        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(VerificationActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(VerificationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        if (user != null && user.isEmailVerified()) {
            Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
