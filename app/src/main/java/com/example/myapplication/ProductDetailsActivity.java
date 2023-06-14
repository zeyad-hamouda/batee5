package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailsActivity";
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;
    private DatabaseReference userRef;
    private FirebaseFirestore firestore;
    private String productDescriptionA;
    private double productPriceA;
    private ImageView closeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImageView = findViewById(R.id.productImageView);
        productNameTextView = findViewById(R.id.productNameTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
        closeButton = findViewById(R.id.closeButton);


        // Get the intent data
        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Set the product details in the views
        productNameTextView.setText(productName);
        Glide.with(this).load(imageUrl).into(productImageView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Get the current user's UID
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a reference to the user's viewed products list in the Realtime Database
        userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userUid).child("viewedProductIds");

        // Query Firestore to get the product ID based on the product name
        firestore = FirebaseFirestore.getInstance();
        Query queryA = firestore.collection("productA").whereEqualTo("name", productName);
        Query queryB = firestore.collection("productB").whereEqualTo("name", productName);

        queryA.get().addOnCompleteListener(taskA -> {
            if (taskA.isSuccessful()) {
                QuerySnapshot querySnapshotA = taskA.getResult();
                if (querySnapshotA != null && !querySnapshotA.isEmpty()) {
                    DocumentSnapshot documentSnapshotA = querySnapshotA.getDocuments().get(0);
                    String productIdA = documentSnapshotA.getId();
                    Log.d(TAG, "Product ID A: " + productIdA);

                    // Check if the product is already in the viewed list
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.hasChild(productIdA)) {
                                // Add the product to the viewed list
                                userRef.child(productIdA).setValue(true);
                                Log.d(TAG, "Product added to viewed list A: " + productIdA);
                            } else {
                                Log.d(TAG, "Product already exists in viewed list A: " + productIdA);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "DatabaseError: " + error.getMessage());
                        }
                    });

                    productDescriptionA = documentSnapshotA.getString("description");
                    productPriceA = documentSnapshotA.getDouble("price");

                    productDescriptionTextView.setText(productDescriptionA);
                    productPriceTextView.setText(String.valueOf(productPriceA));
                } else {
                    Log.e(TAG, "Product not found in Firestore A");
                }
            } else {
                Log.e(TAG, "Error querying Firestore A: " + taskA.getException().getMessage());
            }
        });

        queryB.get().addOnCompleteListener(taskB -> {
            if (taskB.isSuccessful()) {
                QuerySnapshot querySnapshotB = taskB.getResult();
                if (querySnapshotB != null && !querySnapshotB.isEmpty()) {
                    DocumentSnapshot documentSnapshotB = querySnapshotB.getDocuments().get(0);
                    String productIdB = documentSnapshotB.getId();
                    Log.d(TAG, "Product ID B: " + productIdB);

                    // Check if the product is already in the viewed list
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.hasChild(productIdB)) {
                                // Add the product to the viewed list
                                userRef.child(productIdB).setValue(true);
                                Log.d(TAG, "Product added to viewed list B: " + productIdB);
                            } else {
                                Log.d(TAG, "Product already exists in viewed list B: " + productIdB);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "DatabaseError: " + error.getMessage());
                        }
                    });

                    String productDescriptionB = documentSnapshotB.getString("description");
                    double productPriceB = documentSnapshotB.getDouble("price");

                    // Compare the prices and display the cheaper one
                    if (productPriceA < productPriceB) {
                        productPriceTextView.setText(String.valueOf(productPriceA));
                    } else {
                        productPriceTextView.setText(String.valueOf(productPriceB));
                    }

                    productDescriptionTextView.setText(productDescriptionA);
                } else {
                    Log.e(TAG, "Product not found in Firestore B");
                }
            } else {
                Log.e(TAG, "Error querying Firestore B: " + taskB.getException().getMessage());
            }
        });
    }
}
