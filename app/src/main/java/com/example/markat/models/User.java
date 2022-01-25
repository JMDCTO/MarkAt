package com.example.markat.models;

public class User {

    public long id;
    public String email;
    public String password;
    public String alias;
    public String date;

    public User(long id, String email, String alias) {
        this.id = id;
        this.email = email;
        this.alias = alias;
    }

    public User(long id, String email, String password, String alias, String date) {
        this.id = id;
        this.email = email;
        this.alias = alias;

        this.password = password;
        this.date = date;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDateForUi() {

        String[] splitMinus = this.date.split("-");

        for(int i = 0; i < splitMinus.length; i++) {
            if(i == 2) {
                String splitLast = splitMinus[i];
                String[] splitT = splitLast.split("T");
                int finalDayInt = Integer.parseInt(splitT[0]) + 1;
                String finalDay = String.valueOf(finalDayInt);

                return finalDay + "." + splitMinus[1] + "." + splitMinus[0];
            }
        }
        return this.date;
    }
}
