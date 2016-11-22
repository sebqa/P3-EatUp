package com.example.sebastian.appdrawer.appdrawer;

import com.example.sebastian.appdrawer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class Item {

    public String title;
    public String creator; // Owner/Creator of an item
    public String description;
    public int photoId;
    public String creatorLoc; // The location that the item has been associated with
    public int price; // Price per serving
    public int distance; // Distance from the user to the item
    public int amount; // Amount of servings for sale
    public int pickUpTime; // Time for when the item can be picked up

    public Item() {} // Empty constructor, required for Firebase

    //Constructor for list
    public Item(String title, String creator, int price, int photoId) {
        this.title = title;
        this.creator = creator;
        this.price = price;
        this.photoId = photoId;
        this.distance = distance;
    }

    //Constructor for Firebase
    public Item(String user, String title, String description, int cost, int amount) {
        this.creator = user;
        this.title = title;
        this.description = description;
        this.price = cost;
        this.amount = amount;
    }


    //Getters and setters for all values.
    public int getDistance() {return distance;}

    public void setDistance(int distance) {this.distance = distance;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getCreatorLoc() {
        return creatorLoc;
    }

    public void setCreatorLoc(String creatorLoc) {
        this.creatorLoc = creatorLoc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(int pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

}


