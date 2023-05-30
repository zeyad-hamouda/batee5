package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_history);

        productsTextView = findViewById(R.id.products_text_view);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Query query = firestore.collection("products")
                    .whereEqualTo("userId", userId); // Assuming you have a "userId" field in your products collection

            query.addSnapshotListener(this, (value, error) -> {
                if (error != null) {
                    // Handle error
                    return;
                }

                List<String> productNames = new ArrayList<>();
                for (DocumentChange dc : value.getDocumentChanges()) {
                    QueryDocumentSnapshot document = dc.getDocument();
                    String productName = document.getString("name");
                    if (productName != null) {
                        productNames.add(productName);
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
            });
        }
    }
}
