package com.example.myapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String displayName;
    private boolean isSellerAccount;
    private HashMap<String, Boolean> viewedProductIds;
    public User() {
        // Initialize viewedProductIds as an empty HashMap
        this.viewedProductIds = new HashMap<>();
    }


    public User(String email, String phoneNumber, String firstName, String lastName, String dateOfBirth, String displayName, boolean isSellerAccount) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.displayName = displayName;
        this.isSellerAccount = isSellerAccount;
    }
    public User(String email, String phoneNumber, String firstName, String lastName, boolean isSellerAccount, HashMap<String, Boolean> viewedProductIds) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isSellerAccount = isSellerAccount;
        this.viewedProductIds = viewedProductIds;
    }

    public User(String email, String phoneNumber, String firstName, String lastName, boolean isSellerAccount) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isSellerAccount = isSellerAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public boolean isSellerAccount(){ return isSellerAccount;}
    public HashMap<String, Boolean> getViewedProductIds() {
        return viewedProductIds;
    }
    public void setSellerAccount(boolean sellerAccount) {
        isSellerAccount = sellerAccount;
    }

    public void setViewedProductIds(HashMap<String, Boolean> viewedProductIds) {
        this.viewedProductIds = viewedProductIds;
    }
}
