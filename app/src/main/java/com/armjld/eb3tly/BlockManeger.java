package com.armjld.eb3tly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.UnaryOperator;

public class BlockManeger {

    private static ArrayList<String>blockedId = new ArrayList<String>();
    private static long num_Blocked_users=0;
    private static Boolean firstTime = false;

    public static Boolean getFirstTime() {
        return firstTime;
    }

    public static void setFirstTime(Boolean firstTime) {
        BlockManeger.firstTime = firstTime;
    }

    public static long getNum_Blocked_users() {
        return num_Blocked_users;
    }

    public static void setNum_Blocked_users(long num_Blocked_users) {
        BlockManeger.num_Blocked_users = num_Blocked_users;
    }

    public ArrayList<String> getBlockedId() {
        return blockedId;
    }
    public void clear(){
        blockedId.clear();
        num_Blocked_users =0;
    }

    public void adduser(String id){
        blockedId.add((int)num_Blocked_users ,id);
        num_Blocked_users++;
    }
    public void addUser(ArrayList<String>ids){
        blockedId.clear();
        num_Blocked_users = 0;
        for (String id: ids) {
            blockedId.add((int)num_Blocked_users,id);
            num_Blocked_users+=1;
        }

    }



}
