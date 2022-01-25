package com.example.markat.models;

public class UserBillingAddress {

    public String city;
    public String street;
    public String postalNumber;
    public String postalCode;

    private boolean verified;

    public UserBillingAddress(String city, String street, String postalNumber, String postalCode) {

        this.city = city;
        this.street = street;
        this.postalNumber = postalNumber;
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalNumber(String postalNumber) {
        this.postalNumber = postalNumber;
    }

    public String getPostalNumber() {
        return postalNumber;
    }

    public String getLocationName() {
        return this.street + " " + this.postalNumber + ", " + this.postalCode + " " + this.city;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
