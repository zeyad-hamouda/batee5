package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.Query;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private EditText mPhoneNumberEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mDobEditText;
    private Button mSignUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button mLoginButton;
    private EditText mDisplayNameEditText;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        mPhoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        mFirstNameEditText = findViewById(R.id.first_name_edit_text);
        mLastNameEditText = findViewById(R.id.last_name_edit_text);
        mDobEditText = findViewById(R.id.date_of_birth_edit_text);
        mSignUpButton = findViewById(R.id.sign_up_button);
        mDisplayNameEditText = findViewById(R.id.display_name_edit_text);
        CheckBox accountTypeCheckbox = findViewById(R.id.account_type_checkbox);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String confirmPassword = mConfirmPasswordEditText.getText().toString();
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                String firstName = mFirstNameEditText.getText().toString();
                String lastName = mLastNameEditText.getText().toString();
                String dob = mDobEditText.getText().toString();
                String displayName = mDisplayNameEditText.getText().toString();
                boolean isSellerAccount = accountTypeCheckbox.isChecked();

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(dob)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(displayName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter a display name", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                Query query = usersRef.orderByChild("displayName").equalTo(displayName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Display name is already taken!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (!isNewUser) {
                                        Toast.makeText(SignUpActivity.this, "Email already exists, please use a different email", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        Intent intent = new Intent(SignUpActivity.this, EmailVerificationActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        intent.putExtra("phoneNumber", phoneNumber);
                                        intent.putExtra("firstName", firstName);
                                        intent.putExtra("lastName", lastName);
                                        intent.putExtra("dob", dob);
                                        intent.putExtra("displayName", displayName);
                                        intent.putExtra("isSellerAccount", isSellerAccount);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignUpActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
