package com.example.sornanun.tukbard;

/**
 * Created by SORNANUN on 5/6/2560.
 */

public class Monk {

    String myLat;
    String myLong;
    String myAddress;
    String upDateTime;
    String monkUser;

    public Monk(String myLat, String myLong, String myaddress, String upDateTime, String monkUser) {
        this.myLat = myLat;
        this.myLong = myLong;
        this.myAddress = myaddress;
        this.upDateTime = upDateTime;
        this.monkUser = monkUser;
    }

    public String getMyLat() {
        return myLat;
    }

    public String getMyLong() {
        return myLong;
    }

    public String getMyAddress() {
        return myAddress;
    }

    public String getUpDateTime() {
        return upDateTime;
    }

    public String getMonkUser() {
        return monkUser;
    }
}
