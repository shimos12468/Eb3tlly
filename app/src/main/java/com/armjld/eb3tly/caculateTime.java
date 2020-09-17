package com.armjld.eb3tly;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class caculateTime {

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String stopDate = format.format(new Date());


    public String setPostDate(String startDate) {
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(startDate);
            d2 = format.parse(stopDate);
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        assert d2 != null;
        assert d1 != null;
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        int dS = (int) diffSeconds;
        int dM = (int) diffMinutes;
        int dH = (int) diffHours;
        int dD = (int) diffDays;

        String finalDate = "";
        if (dS < 60) {
            if(dS ==1) {
                finalDate = "منذ ثانية";
            } else if(dS == 2) {
                finalDate = "منذ ثانتين";
            } else if (dS > 3 && dS <= 9) {
                finalDate = "منذ " + dS + " ثواني";
            } else {
                finalDate = "منذ " + dS + " ثانية";
            }
        } else if (dS > 60 && dS < 3600) {
            if(dM ==1) {
                finalDate = "منذ دقيقة";
            } else if(dM == 2) {
                finalDate = "منذ ثانتين";
            } else if (dM > 3 && dM <= 9) {
                finalDate = "منذ " + dM + " دقائق";
            } else {
                finalDate = "منذ " + dM + " دقيقة";
            }
        } else if (dS > 3600 && dS < 86400) {
            if(dH ==1) {
                finalDate = "منذ ساعة";
            } else if(dH == 2) {
                finalDate = "منذ ساعتين";
            } else if (dH > 3 && dH <= 9) {
                finalDate = "منذ " + dH + " ساعات";
            } else {
                finalDate = "منذ " + dH + " ساعة";
            }
        } else if (dS > 86400) {
            if(dD ==1) {
                finalDate = "منذ يوم";
            } else if(dD == 2) {
                finalDate = "منذ يومين";
            } else if (dD > 3 && dD <= 9) {
                finalDate = "منذ " + dD + " أيام";
            } else {
                finalDate = "منذ " + dD + " يوم";
            }
        }
       return finalDate;
    }
}
