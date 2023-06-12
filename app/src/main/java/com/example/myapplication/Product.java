package com.example.myapplication;

import java.util.ArrayList;

public class Product {
    private String name;
    private String id;
    private String imageUrl;
    private String category;
    private ArrayList<String> viewerUserIds;

    public Product() {
    }
    public Product(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
    public Product(String name, String imageUrl, ArrayList<String> viewerUserIds) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.viewerUserIds = viewerUserIds;
    }
    public ArrayList<String> getViewerUserIds() {
        return viewerUserIds;
    }
    public void setViewerUserIds(ArrayList<String> viewerUserIds) {
        this.viewerUserIds = viewerUserIds;
    }

    public String getName() {

        return name;
    }

    public String getImageUrl() {

        return imageUrl;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
}

