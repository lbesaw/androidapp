package com.example.mord.myapplication;

/**
 * Created by narhwal on 3/7/2017.
 */

public class Assessment {
    private String type;
    private String note;
    private String course;
    private int day, month, year;

    public void setDate(int day, int month, int year){
        this.day = day;
        this.year = year;
        this.month = month;
    }
    public String getDateAsString() {
        return month+"/"+day+"/"+year;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public void setDueDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    public String toString() {
        return type+" | Due: "+getDateAsString();
    }
}
