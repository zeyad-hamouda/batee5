package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton searchButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        db = FirebaseFirestore.getInstance();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = searchEditText.getText().toString().trim();

                Query productAQuery = db.collection("productA").whereEqualTo("name", searchTerm);
                performQuery(productAQuery);

                Query productBQuery = db.collection("productB").whereEqualTo("name", searchTerm);
                performQuery(productBQuery);
            }
        });
    }

    private void performQuery(Query query) {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Product> productList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        String productName = document.getString("name");
                        String imageUrl = document.getString("image");
                        Product product = new Product(productName, imageUrl);
                        productList.add(product);
                    }
                    displaySearchResults(productList);
                } else {
                    Toast.makeText(SearchActivity.this, "Error performing search", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displaySearchResults(List<Product> productList) {
        RecyclerView searchRecyclerView = findViewById(R.id.searchRecyclerView);
        ProductAdapter productAdapter = new ProductAdapter(productList);
        searchRecyclerView.setAdapter(productAdapter);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}

