package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private Button mDeleteButton;
    private Button mEditAccountButton;
    private Button mAddProducts;
    private Button mProductHistory;
    private TextView mWelcome;
    private boolean isSellerAccount = false;
    private ImageButton mBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton accountButton = findViewById(R.id.account_button);
        mDeleteButton = findViewById(R.id.delete_button);
        mEditAccountButton = findViewById(R.id.edit_account_button);
        mWelcome = findViewById(R.id.welcome_message);
        mAddProducts = findViewById(R.id.add_products_button);
        mProductHistory = findViewById(R.id.previously_added_button);
        mBackButton = findViewById(R.id.back_button);


        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the account activity
                Intent intent = new Intent(AccountActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mEditAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, EditAccountActivity.class);
                startActivity(intent);
            }
        });
        mAddProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, AddProductsActivity.class);
                startActivity(intent);
            }
        });
        mProductHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, ProductHistoryActivity.class);
                startActivity(intent);
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        TextView welcomeMessage = findViewById(R.id.welcome_message);
        Button loginButton = findViewById(R.id.login_button);
        Button logoutButton = findViewById(R.id.logout_button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        isSellerAccount = userSnapshot.child("sellerAccount").getValue(Boolean.class);
                        if (firstName != null && !firstName.isEmpty()) {
                            welcomeMessage.setText("Welcome, " + firstName);
                        } else {
                            welcomeMessage.setText("Welcome");
                        }
                        break;
                    }
                    if (isSellerAccount) {
                        mAddProducts.setVisibility(View.VISIBLE);
                        mProductHistory.setVisibility(View.VISIBLE);
                    } else {
                        mAddProducts.setVisibility(View.GONE);
                        mProductHistory.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            welcomeMessage.setText("Welcome");
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mEditAccountButton.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.GONE);
            mEditAccountButton.setVisibility(View.GONE);
            mWelcome.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the login activity
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                // Redirect the user to the main activity or any other desired screen
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Delete user from Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseRef.removeValue();

        // Delete user from Authentication
        user.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Deletion successful
                        Toast.makeText(AccountActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to desired activity (e.g., MainActivity)
                        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Deletion failed
                        Toast.makeText(AccountActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
