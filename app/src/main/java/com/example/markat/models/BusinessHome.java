package com.example.markat.models;

public class BusinessHome {
    String id;
    String alias;

    public BusinessHome(String id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}
