package com.example.mord.myapplication;

/**
 * This is the term object, containing all of the fields necessary to serialize a database object
 */

public class Term {

    private String termTitle;
    private int startDay, startMonth, startYear;
    private int endDay, endMonth, endYear;

    public Term(String termTitle, Course[] courses, int startDay, int startMonth, int startYear,
                int endDay, int endMonth, int endYear) {
        this.termTitle = termTitle;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;

    }

    public Term() {
        super();
    }

    public String getTermTitle() {
        return termTitle;
    }

    public void setTermTitle(String termTitle) {
        this.termTitle = termTitle;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setStartDate(int startDay, int startMonth, int startYear) {
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public void setEndDate(int endDay, int endMonth, int endYear) {
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public String toString() {
        return termTitle;
    }
}
