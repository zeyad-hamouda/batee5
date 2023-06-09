package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAccountActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPhoneNumberEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private CheckBox mSellerAccountCheckBox;
    private CheckBox mCustomerAccountCheckBox;
    private Button mSaveButton;


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mPhoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        mFirstNameEditText = findViewById(R.id.first_name_edit_text);
        mLastNameEditText = findViewById(R.id.last_name_edit_text);
        mSellerAccountCheckBox = findViewById(R.id.sellerAccountCheckBox);
        mCustomerAccountCheckBox = findViewById(R.id.customerAccountCheckBox);
        mSaveButton = findViewById(R.id.save_button);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the current account details from the database
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the account details from the snapshot
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Pre-fill the EditText fields with the current account details
                            mEmailEditText.setText(user.getEmail());
                            mPhoneNumberEditText.setText(user.getPhoneNumber());
                            mFirstNameEditText.setText(user.getFirstName());
                            mLastNameEditText.setText(user.getLastName());
                            mSellerAccountCheckBox.setChecked(user.isSellerAccount());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the database read error
                    Toast.makeText(EditAccountActivity.this, "Failed to retrieve account details.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    showConfirmationDialog();
                } else {
                    Toast.makeText(EditAccountActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Disable text selection in EditText fields
        disableTextSelection(mEmailEditText);
        disableTextSelection(mPhoneNumberEditText);
        disableTextSelection(mFirstNameEditText);
        disableTextSelection(mLastNameEditText);

        // Set the back button listener
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithSlideAnimation();
            }
        });
    }

    private boolean validateFields() {
        String email = mEmailEditText.getText().toString().trim();
        String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
        String firstName = mFirstNameEditText.getText().toString().trim();
        String lastName = mLastNameEditText.getText().toString().trim();

        return !email.isEmpty() && !phoneNumber.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes");
        builder.setMessage("Are you sure you want to save these changes?");

        // Set the positive button (Save)
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateAccountDetails();
            }
        });

        // Set the negative button (Cancel)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel the dialog and do nothing
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Add the update functionality to apply changes to authentication and the realtime database
    private void updateAccountDetails() {
        // Retrieve the modified account details from the EditText fields
        String email = mEmailEditText.getText().toString().trim();
        String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
        String firstName = mFirstNameEditText.getText().toString().trim();
        String lastName = mLastNameEditText.getText().toString().trim();
        boolean isSellerAccount = mSellerAccountCheckBox.isChecked();

        // Update the account details in authentication (FirebaseAuth) if needed
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email update successful
                                // Continue with updating the account details in the realtime database
                                updateAccountDetailsInDatabase(email, phoneNumber, firstName, lastName, isSellerAccount);
                            } else {
                                // Email update failed
                                Toast.makeText(EditAccountActivity.this, "Failed to update email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateAccountDetailsInDatabase(String email, String phoneNumber, String firstName, String lastName, boolean isSellerAccount) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the existing user data from the database
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get the existing user object from the snapshot
                        User existingUser = snapshot.getValue(User.class);
                        if (existingUser != null) {
                            // Update the specific fields with the new values
                            existingUser.setEmail(email);
                            existingUser.setPhoneNumber(phoneNumber);
                            existingUser.setFirstName(firstName);
                            existingUser.setLastName(lastName);
                            existingUser.setSellerAccount(isSellerAccount);

                            // Update the modified user object in the database
                            mDatabase.child(userId).setValue(existingUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Account details update successful
                                                Toast.makeText(EditAccountActivity.this, "Account details updated successfully.", Toast.LENGTH_SHORT).show();
                                                finishWithSlideAnimation(); // Close the activity and return to the previous screen with slide animation
                                            } else {
                                                // Account details update failed
                                                Toast.makeText(EditAccountActivity.this, "Failed to update account details.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the database read error
                    Toast.makeText(EditAccountActivity.this, "Failed to retrieve account details.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void disableTextSelection(EditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        editText.setLongClickable(false);
        editText.setTextIsSelectable(false);
    }

    private void finishWithSlideAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
