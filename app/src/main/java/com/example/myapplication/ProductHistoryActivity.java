// ProductHistoryActivity.java
package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductHistoryActivity extends AppCompatActivity {

    private TextView productsTextView;
    private ImageView productImageView;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_history);

        productsTextView = findViewById(R.id.products_text_view);
        productImageView = findViewById(R.id.product_image_view);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        closeButton = findViewById(R.id.closeButton);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Query query = firestore.collection("productA")
                    .whereEqualTo("userId", userId); // Assuming you have a "userId" field in your products collection

            query.addSnapshotListener(this, (value, error) -> {
                if (error != null) {
                    // Handle error
                    return;
                }

                List<String> productNames = new ArrayList<>();
                String productImageUrl = ""; // Variable to store the product image URL
                for (DocumentChange dc : value.getDocumentChanges()) {
                    QueryDocumentSnapshot document = dc.getDocument();
                    String productName = document.getString("name");
                    if (productName != null) {
                        productNames.add(productName);
                        productImageUrl = document.getString("image"); // Assuming you have an "image" field in your products collection for storing the image URL
                    }
                }

                if (productNames.isEmpty()) {
                    productsTextView.setText("You haven't added any products yet.");
                } else {
                    String productsText = "Your added products:\n";
                    for (String productName : productNames) {
                        productsText += "- " + productName + "\n";
                    }
                    productsTextView.setText(productsText);
                }

                // Load the product image using Glide library
                if (productImageUrl != null && !productImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(productImageUrl)
                            .into(productImageView);
                }
            });
        }
    }
}
