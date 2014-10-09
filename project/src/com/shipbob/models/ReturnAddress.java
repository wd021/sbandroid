package com.shipbob.models;

/**
 * Created by waldemar on 11.06.14.
 */
public class ReturnAddress {

    private String address;
    private String city;
    private String state;
    private String streetAddress2;
    private int zip;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String address) {
        this.streetAddress2 = address;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }
}
