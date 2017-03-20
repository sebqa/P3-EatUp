package com.example.sebastian.appdrawer;


/**
 * Created by Sebastian on 02-11-2016.
 */

public class Item {

    public String title;
    public String creator; // Owner/Creator of an item
    public String description;
    public String key;
    public String username;
    public String userID;
    public String currentTime;
    public String address;
    public String price; // Price per serving
    public double distance; // Distance from the user to the item
    public String downloadUrl;
    public int amount; // Amount of servings for sale
    //public int pickUpTime; // Time for when the item can be picked up
    public double latitude; // Latitude position of the item when it was created
    public double longitude; // Longitude position of the item when it was created
    public Item() {} // Empty constructor, required for Firebase

    //Constructor for list
    public Item(String title, String creator, String price, int amount) {
        this.title = title;
        this.creator = creator;
        this.price = price;
        this.amount = amount;
    }

    //Constructor for Firebase
    public Item(String user, String title, String description, String cost, int amount,
                String currentTime, String key, String downloadUrl,
                double latitude, double longitude, String userID,String address) {
        this.creator = user;
        this.title = title;
        this.description = description;
        this.price = cost;
        this.amount = amount;
        this.currentTime = currentTime;
        this.key = key;
        this.downloadUrl = downloadUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userID = userID;
        this.address = address;
    }


    public String getTitle() {
        return title;
    }
    public String getCreator() {
        return creator;
    }
    public String getPrice() {
        return price;
    }
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
    public String getKey() {
        return key;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {this.userID = userID;}
    public void setKey(String key) {
        this.key = key;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {return distance;}

    public void setDistance(double distance) {this.distance = distance;}

    public void setTitle(String title) {
        this.title = title;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

}


