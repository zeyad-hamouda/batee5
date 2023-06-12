package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private DatabaseReference userRef;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImageView = findViewById(R.id.productImageView);
        productNameTextView = findViewById(R.id.productNameTextView);

        // Get the intent data
        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Set the product details in the views
        productNameTextView.setText(productName);
        Glide.with(this).load(imageUrl).into(productImageView);

        // Get the current user's email
        // Get the current user's UID
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a reference to the user's viewed products list in the Realtime Database
        userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userUid).child("viewedProductIds");

        // Query Firestore to get the product ID based on the product name
        firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("productA").whereEqualTo("name", productName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    String productId = documentSnapshot.getId();
                    Log.d(TAG, "Product ID: " + productId);

                    // Check if the product is already in the viewed list
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.hasChild(productId)) {
                                // Add the product to the viewed list
                                userRef.child(productId).setValue(true);
                                Log.d(TAG, "Product added to viewed list: " + productId);
                            } else {
                                Log.d(TAG, "Product already exists in viewed list: " + productId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "DatabaseError: " + error.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "Product not found in Firestore");
                }
            } else {
                Log.e(TAG, "Error querying Firestore: " + task.getException().getMessage());
            }
        });
    }
}
