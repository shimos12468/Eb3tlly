package com.armjld.eb3tly.LocationManeger;

public class LocationDataType {

    private double lontude;
    private double lattude;
    private String address;
    private String region;
    private String state;
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLontude() {
        return lontude;
    }

    public void setLontude(double lontude) {
        this.lontude = lontude;
    }

    public double getLattude() {
        return lattude;
    }

    public void setLattude(double lattude) {
        this.lattude = lattude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
