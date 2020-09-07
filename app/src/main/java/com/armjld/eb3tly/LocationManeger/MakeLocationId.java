package com.armjld.eb3tly.LocationManeger;

public class MakeLocationId {

    private String locationid = "";

    public String getLocationid() {
        return locationid;
    }

    public MakeLocationId(double lonitude , double lititude) {
        locationid += Double.toString(lonitude);
        locationid += Double.toString(lititude);

    }
}
