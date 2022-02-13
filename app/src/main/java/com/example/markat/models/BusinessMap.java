package com.example.markat.models;

public class BusinessMap {

    String id;
    String official;
    String alias;
    String street;
    String number;
    String postal;

    String latitude;
    String longitude;

    public BusinessMap(String id, String official, String alias, String street, String number, String postal, String lat, String longitude) {
        this.id = id;
        this.alias = alias;
        this.official = official;
        this.street = street;
        this.number = number;
        this.postal = postal;
        this.latitude = lat;
        this.longitude = longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getPostal() {
        return postal;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    public String getOfficial() {
        return official;
    }
}
