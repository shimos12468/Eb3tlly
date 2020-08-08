package com.armjld.eb3tly;

public class UserInFormation {
    private static String userName;
    private static String AccountType;
    private static String userDate;
    private static String userURL;
    private static String id;

    public UserInFormation() {
    }

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

    public static void setId(String id) {
        UserInFormation.id = id;
    }
}
