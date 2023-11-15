package com.example.eventmaster;

public class FavouriteClass {
    int userID, eventID, favouriteID;

    public FavouriteClass() {
    }

    public FavouriteClass(int favouriteID, int userID, int eventID) {
        this.favouriteID = favouriteID;
        this.userID = userID;
        this.eventID = eventID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getFavouriteID() {
        return favouriteID;
    }

    public void setFavouriteID(int favouriteID) {
        this.favouriteID = favouriteID;
    }
}
