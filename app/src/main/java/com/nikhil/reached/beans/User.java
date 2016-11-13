package com.nikhil.reached.beans;

/**
 * Created by nikhil on 07/11/16.
 */

public class User {

    private String id;

    public User() {
    }

    private static User myUser;

    public static User getMyUser() {

        if (myUser == null) {
            myUser = new User();
        }
        return myUser;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public User(String id, String mobileNo, String firebaseRegid, String userName, String userEmail) {
        this.id = id;
        this.mobileNo = mobileNo;
        this.firebaseRegid = firebaseRegid;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    private String mobileNo;
    private String firebaseRegid;
    private String userName;
    private String userEmail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFirebaseRegid() {
        return firebaseRegid;
    }

    public void setFirebaseRegid(String firebaseRegid) {
        this.firebaseRegid = firebaseRegid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
