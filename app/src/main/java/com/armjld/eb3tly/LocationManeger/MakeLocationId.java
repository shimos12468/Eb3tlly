package com.armjld.eb3tly.LocationManeger;

public class MakeLocationId {

    private String locationid = "";
    private  String lol = "";
    public String getLocationid() {
        return locationid;
    }

    public MakeLocationId(double lonitude , double lititude) {
        locationid += Double.toString(lonitude);
        locationid += Double.toString(lititude);

        adjust();
    }
    public void adjust(){
        for(int i = 0;i<locationid.length();i++){
            if(locationid.charAt(i)!= '.'){
                 lol+=locationid.charAt(i);
            }
        }
        locationid=lol;
    }
}
