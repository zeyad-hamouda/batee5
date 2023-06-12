package com.example.myapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationEngine {
    private Map<String, User> users;
    private Map<String, Product> products;

    public RecommendationEngine(Map<String, User> users, Map<String, Product> products) {
        this.users = users;
        this.products = products;
    }

    public List<Product> recommendProductsForUser(User user) {
        List<Product> recommendations = new ArrayList<>();

        // Collaborative filtering: Find users who have viewed the same products
        for (String viewedProductId : user.getViewedProductIds().keySet()) {
            Product viewedProduct = products.get(viewedProductId);

            // For each product that the viewerUser has viewed, add it to the recommendations
            for (String productId : viewedProduct.getViewerUserIds()) {
                if (!user.getViewedProductIds().containsKey(productId)) {
                    recommendations.add(products.get(productId));
                }
            }
        }

        // Content-based filtering: Recommend products in the same categories
        for (Product product : products.values()) {
            if (!user.getViewedProductIds().containsKey(product.getId())) {
                for (String viewedProductId : user.getViewedProductIds().keySet()) {
                    Product viewedProduct = products.get(viewedProductId);

                    if (viewedProduct.getCategory().equals(product.getCategory())) {
                        recommendations.add(product);
                        break;
                    }
                }
            }
        }

        return recommendations;
    }
}
