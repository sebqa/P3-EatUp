package com.example.sebastian.appdrawer.appdrawer;

import com.example.sebastian.appdrawer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class Item {



    public String title;
    public String creator;
    int photoId;
    public String creatorLoc;
    public String price;
    public int distance;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int amount;
    public int pickUpTime;


    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Item(String title, String creator, String price, int photoId) {
        this.title = title;
        this.creator = creator;
        this.price = price;
        this.photoId = photoId;
        this.distance = distance;
    }

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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


