package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = searchEditText.getText().toString().trim();
                ProductBot.searchProducts(searchTerm, new ProductBot.SearchListener() {
                    @Override
                    public void onSearchComplete(List<ProductBot.ProductInfo> products) {
                        displaySearchResults(products);
                    }

                    @Override
                    public void onSearchError(String errorMessage) {
                        Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void displaySearchResults(List<ProductBot.ProductInfo> productInfoList) {
        List<Product> productList = convertToProductList(productInfoList);

        RecyclerView searchRecyclerView = findViewById(R.id.searchRecyclerView);
        ProductAdapter productAdapter = new ProductAdapter(productList);
        searchRecyclerView.setAdapter(productAdapter);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Product> convertToProductList(List<ProductBot.ProductInfo> productInfoList) {
        List<Product> productList = new ArrayList<>();
        for (ProductBot.ProductInfo productInfo : productInfoList) {
            String productName = productInfo.getTitle();
            Product product = new Product(productName);
            productList.add(product);
        }
        return productList;
    }
}
