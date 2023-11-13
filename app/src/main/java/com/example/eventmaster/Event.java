package com.example.eventmaster;


import java.sql.Blob;

public class Event {
    int eventID, userID, status;
    long enddatetime, startdatetime;
    String location, description,type,name;
    byte[] image;

    public Event() {
    }

    public Event(int eventID, long enddatetime, long startdatetime, String location, String description, String type, String name, int userID, byte[] image, int status) {
        this.eventID = eventID;
        this.enddatetime = enddatetime;
        this.startdatetime = startdatetime;
        this.location = location;
        this.description = description;
        this.type = type;
        this.name = name;
        this.userID = userID;
        this.image = image;
        this.status = status;

    }

    public Event(long enddatetime, long startdatetime, String location, String description, String type, String name, byte[] image, int status) {
        this.enddatetime = enddatetime;
        this.startdatetime = startdatetime;
        this.location = location;
        this.description = description;
        this.type = type;
        this.name = name;
        this.image = image;
        this.status = status;
    }



    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public long getEnddatetime() {
        return enddatetime;
    }

    public void setEnddatetime(long enddatetime) {
        this.enddatetime = enddatetime;
    }

    public long getStartdatetime() {
        return startdatetime;
    }

    public void setStartdatetime(long startdatetime) {
        this.startdatetime = startdatetime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
