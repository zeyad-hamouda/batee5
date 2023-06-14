package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private ImageButton backButton;
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        backButton = findViewById(R.id.backButton);

        db = FirebaseFirestore.getInstance();
        productAdapter = new ProductAdapter(new ArrayList<>());
        productAdapter.setOnProductClickListener(this);

        searchRecyclerView.setAdapter(productAdapter);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEditText.requestFocus();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called while the text is being changed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called after the text has been changed
                String searchTerm = editable.toString().trim().toLowerCase();

                if (searchTerm.isEmpty()) {
                    // Clear the product list when the search term is empty
                    productAdapter.setProducts(new ArrayList<>());
                    productAdapter.notifyDataSetChanged();
                    return;
                }

                // Perform the query with a dynamic filter
                Query productAQuery = db.collection("productA")
                        .whereGreaterThanOrEqualTo("lowercaseName", searchTerm)
                        .whereLessThan("lowercaseName", searchTerm + "z");

                performQuery(productAQuery);

                Query productBQuery = db.collection("productB")
                        .whereGreaterThanOrEqualTo("lowercaseName", searchTerm)
                        .whereLessThan("lowercaseName", searchTerm + "z");

                performQuery(productBQuery);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithSlideAnimation();
            }
        });
    }

    private void performQuery(Query query) {
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(SearchActivity.this, "Error performing search", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Product> productList = new ArrayList<>();
                for (DocumentSnapshot document : value) {
                    String productName = document.getString("name");
                    String imageUrl = document.getString("image");
                    Product product = new Product(productName, imageUrl);
                    productList.add(product);
                }

                productAdapter.setProducts(productList);
                productAdapter.notifyDataSetChanged();
            }
        });
    }

    private void finishWithSlideAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
        intent.putExtra("productName", product.getName());
        intent.putExtra("imageUrl", product.getImageUrl());
        startActivityWithSlideAnimation(intent);
    }

    private void startActivityWithSlideAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
