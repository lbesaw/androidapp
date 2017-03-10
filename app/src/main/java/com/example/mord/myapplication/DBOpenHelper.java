package com.example.mord.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "helperApp.db";
    private static final int DATABASE_VERSION = 2;

    //Constants for identifying 'terms' table and columns
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_TITLE = "termTitle";
    public static final String TERM_START_DAY = "termStartDay";
    public static final String TERM_START_MONTH = "termStartMonth";
    public static final String TERM_START_YEAR = "termStartYear";
    public static final String TERM_END_DAY = "termEndDay";
    public static final String TERM_END_MONTH = "termEndMonth";
    public static final String TERM_END_YEAR = "termEndYear";

    //Constants for identifying 'courses' table and columns
    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TERM = "courseInTerm";
    public static final String COURSE_TITLE = "courseTitle";
    public static final String COURSE_STATUS = "courseStatus";
   public static final String COURSE_MENTOR = "courseMentor";
    public static final String COURSE_TEXT_NOTES = "courseTextNotes";
    public static final String COURSE_PICTURE_NOTES = "coursePictureNotes";
    public static final String COURSE_START_DAY = "startDay";
    public static final String COURSE_START_MONTH = "startMonth";
    public static final String COURSE_START_YEAR = "startYear";
    public static final String COURSE_END_DAY = "endDay";
    public static final String COURSE_END_MONTH = "endMonth";
    public static final String COURSE_END_YEAR = "endYear";

    //Constants for identifying 'assessments' table and columns

    //Constants for making 'mentor' table
    public static final String TABLE_MENTORS = "mentors";
    public static final String MENTOR_NAME = "mentorName";
    public static final String MENTOR_PHONE = "mentorPhone";
    public static final String MENTOR_EMAIL = "mentorEmail";


    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_COURSE = "assessmentInCourse";
    public static final String ASSESSMENT_TYPE = "assessmentType";
    public static final String ASSESSMENT_TEXT_NOTES = "assessmentTextNotes";
    public static final String ASSESSMENT_PIC_NOTES = "assessmentPictureNotes";
    public static final String ASSESSMENT_DUE_DAY = "startDay";
    public static final String ASSESSMENT_DUE_MONTH = "startMonth";
    public static final String ASSESSMENT_DUE_YEAR = "startYear";



    //SQL for creating assessments table
    public static final String ASSESSMENTS_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" +
                    ASSESSMENT_ID + " TEXT, " +
                    ASSESSMENT_COURSE + " TEXT, " +
                    ASSESSMENT_TYPE + " TEXT, " +
                    ASSESSMENT_TEXT_NOTES + " TEXT, " +
                    ASSESSMENT_PIC_NOTES + " BLOB, " +
                    ASSESSMENT_DUE_DAY + " INTEGER, " +
                    ASSESSMENT_DUE_MONTH + " INTEGER, " +
                    ASSESSMENT_DUE_YEAR + " INTEGER, " +
                    "FOREIGN KEY ("+ASSESSMENT_COURSE+") REFERENCES "+
                    TABLE_COURSES+"("+COURSE_TITLE+") "+
                    ")";


    //SQL for creating courses table
    private static final String COURSES_TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_TERM + " TEXT, " +
                    COURSE_TITLE + " TEXT, " +
                    COURSE_STATUS + " TEXT, " +
                    COURSE_TEXT_NOTES + " TEXT, " +
                    COURSE_PICTURE_NOTES + " BLOB, " +
                    COURSE_START_DAY + " INTEGER, " +
                    COURSE_START_MONTH + " INTEGER, " +
                    COURSE_START_YEAR + " INTEGER, " +
                    COURSE_END_DAY + " INTEGER, " +
                    COURSE_END_MONTH + " INTEGER, " +
                    COURSE_END_YEAR + " INTEGER, "+
                    COURSE_MENTOR + " TEXT, " +
                    "FOREIGN KEY ("+COURSE_MENTOR+") REFERENCES "+
                    TABLE_MENTORS+"("+MENTOR_NAME+") "+
                    "FOREIGN KEY ("+COURSE_TERM+") REFERENCES "+
                    TABLE_TERMS+"("+TERM_TITLE+") "+
                    ")";

    public static final String MENTORS_TABLE_CREATE =
            "CREATE TABLE "+TABLE_MENTORS+" ("+
                    MENTOR_NAME + " TEXT PRIMARY KEY, " +
                    MENTOR_PHONE + " TEXT, " +
                    MENTOR_EMAIL + " TEXT " +
                    ")";


    //SQL to create term table
    private static final String TERMS_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TERMS + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START_DAY + " TEXT, " +
                    TERM_START_MONTH + " TEXT, " +
                    TERM_START_YEAR + " TEXT, " +
                    TERM_END_DAY + " TEXT, " +
                    TERM_END_MONTH + " TEXT, " +
                    TERM_END_YEAR + " TEXT" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TERMS_TABLE_CREATE);
        db.execSQL(COURSES_TABLE_CREATE);
        db.execSQL(ASSESSMENTS_TABLE_CREATE);
        db.execSQL(MENTORS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTORS);
        onCreate(db);
    }
}
