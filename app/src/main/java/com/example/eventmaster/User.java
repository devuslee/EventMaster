package com.example.eventmaster;


public class User {
    int userID, role;
    String email, password, phone, username;
    byte[] profileimage;

    public User() {
    }

    public User(int userID, String email, String password, String phone, String username, byte[] profileimage, int role) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.username = username;
        this.profileimage = profileimage;
        this.role = role;
    }

    public User(String email, String password, String phone, String username, byte[] profileimage, int role) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.username = username;
        this.profileimage = profileimage;
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(byte[] profileimage) {
        this.profileimage = profileimage;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}