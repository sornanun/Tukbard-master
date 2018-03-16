package com.example.sornanun.tukbard;

import java.util.Objects;

/**
 * Created by SORNANUN on 7/6/2560.
 */

public class PraDate {

    int date;
    int month;
    int year;
    String detail;
    String type;

    public PraDate(int date, int month, int year, String detail, String type) {
        this.date = date;
        this.month = month;
        this.year = year;
        this.detail = detail;
        this.type = type;
    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getDetail() {
        return detail;
    }

    public String getType() {
        return type;
    }

}
