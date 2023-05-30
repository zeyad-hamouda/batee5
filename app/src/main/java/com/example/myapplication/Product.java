package com.example.myapplication;

public class Product {
    private String name;
    private String imageUrl;

    public Product() {
    }

    public Product(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

