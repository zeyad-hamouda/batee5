package com.example.myapplication;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.*;

public class ProductBot {

    interface SearchListener {
        void onSearchComplete(List<ProductInfo> products);
        void onSearchError(String errorMessage);
    }

    static class ProductInfo {
        private String title;
        private String description;
        private String price;
        private String category;

        // Constructor
        public ProductInfo(String title, String description, String price, String category) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.category = category;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPrice() {
            return price;
        }

        public String getCategory() {
            return category;
        }

        // Setters
        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public static void searchProducts(String searchProducts, SearchListener listener) {
        new SearchTask(searchProducts, listener).execute();
    }

    private static class SearchTask extends AsyncTask<Void, Void, List<ProductInfo>> {
        private String searchProducts;
        private SearchListener listener;
        private static final int MAX_RETRY_COUNT = 3;
        private static final int RETRY_DELAY_MS = 2000; // 2 seconds
        private static final int TIMEOUT_SECONDS = 90; // 30 seconds

        SearchTask(String searchProducts, SearchListener listener) {
            this.searchProducts = searchProducts;
            this.listener = listener;
        }

        @Override
        protected List<ProductInfo> doInBackground(Void... voids) {
            List<ProductInfo> productInfoList = new ArrayList<>();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            // You would replace "searchTerm" in the body of your request with the actual search term
            RequestBody bodyAmazon = RequestBody.create(mediaType,
                    "{\"source\": \"amazon_ae\", \"query\": \"" + searchProducts + "\"}");
            RequestBody bodyVirginMegastore = RequestBody.create(mediaType,
                    "{\"source\": \"universal_ecommerce\", \"url\": \"https://www.virginmegastore.ae/" + searchProducts + "\"}");

            Request requestAmazon = new Request.Builder()
                    .url("https://realtime.oxylabs.io/v1/queries")
                    .method("POST", bodyAmazon)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", Credentials.basic("zeyadzilzal", "Zezobas1!")) //
                    .build();

            Request requestVirginMegastore = new Request.Builder()
                    .url("https://realtime.oxylabs.io/v1/queries")
                    .method("POST", bodyVirginMegastore)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", Credentials.basic("zeyadzilzal", "Zezobas1!"))
                    .build();
            int retryCount = 0;
            while (retryCount < MAX_RETRY_COUNT) {
                try {
                Response responseAmazon = client.newCall(requestAmazon).execute();
                Response responseVirginMegastore = client.newCall(requestVirginMegastore).execute();

                String responseBodyAmazon = responseAmazon.body().string();
                String responseBodyVirginMegastore = responseVirginMegastore.body().string();

                // Parse the JSON responses to extract the product data
                JSONObject jsonObjectAmazon = new JSONObject(responseBodyAmazon);
                JSONObject jsonObjectVirginMegastore = new JSONObject(responseBodyVirginMegastore);


                // Extract product details from Amazon's response
                JSONArray resultsAmazon = jsonObjectAmazon.getJSONArray("results");
                for (int i = 0; i < resultsAmazon.length(); i++) {
                    JSONObject productObject = resultsAmazon.getJSONObject(i).getJSONObject("content");

                    String title = productObject.getString("title");
                    String description = productObject.getString("description");
                    double price = productObject.getDouble("price");
                    String category = ""; // No category field in the provided sample data
                    ProductInfo productInfo = new ProductInfo(title, description, String.valueOf(price), category);

                    productInfoList.add(productInfo);
                }

                // Extract product details from VirginMegastore's response
                JSONArray resultsVirginMegastore = jsonObjectVirginMegastore.getJSONArray("results");
                for (int i = 0; i < resultsVirginMegastore.length(); i++) {
                    JSONObject productObject = resultsVirginMegastore.getJSONObject(i).getJSONObject("content");

                    String title = productObject.getString("title");
                    String description = productObject.getString("description");
                    double price = productObject.getDouble("price");
                    String category = ""; // No category field in the provided sample data
                    ProductInfo productInfo = new ProductInfo(title, description, String.valueOf(price), category);

                    productInfoList.add(productInfo);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                retryCount++;

                // If the maximum retry count is reached, return null
                if (retryCount == MAX_RETRY_COUNT) {
                    return null;
                }

                // Sleep for a delay before retrying
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }

            return productInfoList;
        }

        @Override
        protected void onPostExecute(List<ProductInfo> products) {
            if (products != null) {
                listener.onSearchComplete(products);
            } else {
                listener.onSearchError("An error occurred while searching for products.");
            }
        }
    }
}
