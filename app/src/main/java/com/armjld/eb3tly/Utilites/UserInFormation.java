package com.armjld.eb3tly.Utilites;

public class UserInFormation {

    private static String userName;
    private static String AccountType;
    private static String userDate;
    private static String userURL;
    private static String id;
    private static String uPhone;
    private static String isConfirm;
    private static String email;
    private static String pass;
    private static int rating = 5;

    public static String getCurrentdate() {
        return currentdate;
    }

    public static void setCurrentdate(String currentdate) { UserInFormation.currentdate = currentdate; }

    private static String currentdate = "none";

    public UserInFormation() { }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserInFormation.userName = userName;
    }

    public static String getAccountType() {
        return AccountType;
    }

    public static void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public static String getUserDate() {
        return userDate;
    }

    public static void setUserDate(String userDate) {
        UserInFormation.userDate = userDate;
    }

    public static String getUserURL() {
        return userURL;
    }

    public static void setUserURL(String userURL) {
        UserInFormation.userURL = userURL;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) { UserInFormation.id = id; }

    public static String getisConfirm() { return isConfirm; }

    public static void setisConfirm(String isConfirm) { UserInFormation.isConfirm = isConfirm; }

    public static String getPhone() { return uPhone; }

    public static void setPhone(String uPhone) { UserInFormation.uPhone = uPhone; }

    public static String getEmail() { return email; }

    public static void setEmail(String email) { UserInFormation.email = email; }

    public static String getPass() { return pass; }

    public static void setPass(String pass) { UserInFormation.pass = pass; }

    public static void setRating(int rating) { UserInFormation.rating = rating; }

    public static int getRating() {
        return rating;
    }
}
