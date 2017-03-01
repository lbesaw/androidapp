package com.example.mord.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by narhwal on 1/31/2017.
 */

public class Course implements Serializable {
    private String courseTitle;
    private String courseStatus;
    private String termTitle;
    private String courseMentor;
    private int startDay, startMonth, startYear;
    private int endDay, endMonth, endYear;
    private List<String> courseAssessments;
    private List<String> courseTextNotes;

    public Course(String courseTitle, String courseStatus, Mentor courseMentor,
                  String[] courseAssessments, String[] courseTextNotes, int startDay,
                  int startMonth, int startYear, int endDay, int endMonth, int endYear) {
        this.courseTitle = courseTitle;
        this.courseStatus = courseStatus;
        this.courseAssessments = Arrays.asList(courseAssessments);
        this.courseTextNotes = Arrays.asList(courseTextNotes);
        this.startDay=startDay;
        this.startMonth=startMonth;
        this.startYear = startYear;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
    }
    public Course() {
        super();
    }
public void setStartDate(int startDay, int startMonth, int startYear) {
    this.startDay = startDay;
    this.startMonth = startMonth;
    this.startYear = startYear;
}
    public void setEndDate(int endDay, int endMonth, int endYear) {
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
    }
    public String getTermTitle() {
        return termTitle;
    }

    public void setTermTitle(String termTitle) {
        this.termTitle = termTitle;
    }
    public void addAssessment(String assessment) {
        courseAssessments.add(assessment);
    }
    public void addNote(String note) {
        courseTextNotes.add(note);
    }
    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
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
    public void setCourseMentor(String mentor) {
        this.courseMentor = mentor;
    }
    public String getCourseMentor() {
        return courseMentor;
    }

    public String toString() {
        return courseTitle + " - "+ startMonth+"/"+startDay+"/"+startYear;
    }
}
