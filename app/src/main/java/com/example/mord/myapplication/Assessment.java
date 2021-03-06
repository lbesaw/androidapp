package com.example.mord.myapplication;

/**
 * Assessment object containing all of the required fields to interface with the database
 */

public class Assessment {
    private String type;
    private String note;
    private String course;
    private String id;
    private String name;
    private int day, month, year;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateAsString() {
        return month + "/" + day + "/" + year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeNo() {
        if (this.type.equals("Objective assessment"))
            return 1;
        if (this.type.equals("Performance assessment"))
            return 0;
        else return -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return name + " | " + type.charAt(0) + " | " + getDateAsString();
    }
}
