package com.example.eventmaster;

public class NotificationClass {

    int notificationID, eventID, userID;
    String notificationMessage, type, time, eventname;

    public NotificationClass() {
    }

    public NotificationClass(int notificationID, int eventID, int userID, String notificationMessage, String type, String time, String eventname) {
        this.notificationID = notificationID;
        this.eventID = eventID;
        this.userID = userID;
        this.notificationMessage = notificationMessage;
        this.type = type;
        this.time = time;
        this.eventname = eventname;
    }

    public NotificationClass(int notificationID, int userID, String notificationMessage,String type, String time, String eventname) {
        this.notificationID = notificationID;
        this.userID = userID;
        this.notificationMessage = notificationMessage;
        this.type = type;
        this.time = time;
        this.eventname = eventname;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }
}