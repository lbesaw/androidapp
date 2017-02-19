package com.example.mord.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBProvider {


    public static final String CONTENT_ITEM_TYPE = "editor";
    public SQLiteDatabase database;
    private SQLiteOpenHelper dbhelper;

    public static final String[] ASSESSMENTS_ALL_COLUMNS = {DBOpenHelper.ASSESSMENT_ID, DBOpenHelper.ASSESSMENT_COURSE, DBOpenHelper.ASSESSMENT_TYPE, DBOpenHelper.ASSESSMENT_TEXT_NOTES, DBOpenHelper.ASSESSMENT_PIC_NOTES, DBOpenHelper.ASSESSMENT_DUE_DAY,
            DBOpenHelper.ASSESSMENT_DUE_MONTH, DBOpenHelper.ASSESSMENT_DUE_YEAR};

    public static final String[] COURSES_ALL_COLUMNS = {DBOpenHelper.COURSE_ID, DBOpenHelper.COURSE_TERM, DBOpenHelper.COURSE_TITLE, DBOpenHelper.COURSE_STATUS,
            DBOpenHelper.COURSE_TEXT_NOTES, DBOpenHelper.COURSE_PICTURE_NOTES, DBOpenHelper.COURSE_START_DAY, DBOpenHelper.COURSE_START_MONTH, DBOpenHelper.COURSE_START_YEAR, DBOpenHelper.COURSE_END_DAY,
            DBOpenHelper.COURSE_END_MONTH, DBOpenHelper.COURSE_END_YEAR, DBOpenHelper.COURSE_MENTOR};

    public static final String[] MENTORS_ALL_COLUMNS = {DBOpenHelper.MENTOR_NAME, DBOpenHelper.MENTOR_PHONE, DBOpenHelper.MENTOR_EMAIL};

    public static final String[] TERMS_ALL_COLUMNS = {DBOpenHelper.TERM_ID, DBOpenHelper.TERM_TITLE, DBOpenHelper.TERM_START_DAY, DBOpenHelper.TERM_START_MONTH, DBOpenHelper.TERM_START_YEAR, DBOpenHelper.TERM_END_DAY, DBOpenHelper.TERM_END_MONTH, DBOpenHelper.TERM_END_YEAR};

    public DBProvider(Context context) {
        dbhelper = new DBOpenHelper(context);
    }

    public void open(){database=dbhelper.getWritableDatabase();}


    public void close() { dbhelper.close();}

    public void add(Term term) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, term.getTermTitle());
        values.put(DBOpenHelper.TERM_START_DAY, term.getStartDay());
        values.put(DBOpenHelper.TERM_START_MONTH, term.getStartMonth());
        values.put(DBOpenHelper.TERM_START_YEAR, term.getStartYear());
        values.put(DBOpenHelper.TERM_END_DAY, term.getEndDay());
        values.put(DBOpenHelper.TERM_END_MONTH, term.getEndMonth());
        values.put(DBOpenHelper.TERM_END_YEAR, term.getEndYear());
        database.insert(DBOpenHelper.TABLE_TERMS, null, values);
    }

    public void update(String oldTerm, Term term) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, term.getTermTitle());
        values.put(DBOpenHelper.TERM_START_DAY, term.getStartDay());
        values.put(DBOpenHelper.TERM_START_MONTH, term.getStartMonth());
        values.put(DBOpenHelper.TERM_START_YEAR, term.getStartYear());
        values.put(DBOpenHelper.TERM_END_DAY, term.getEndDay());
        values.put(DBOpenHelper.TERM_END_MONTH, term.getEndMonth());
        values.put(DBOpenHelper.TERM_END_YEAR, term.getEndYear());
        database.update(DBOpenHelper.TABLE_TERMS, values, DBOpenHelper.TERM_TITLE +"=\""+oldTerm+"\"", null);
    }

    public Term getTerm(String termName) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_TERMS, TERMS_ALL_COLUMNS, DBOpenHelper.TERM_TITLE + " = ?", new String[] {termName}, null, null, null);
        Term term = new Term();

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                term.setTermTitle(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE)));
                term.setStartDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_START_DAY)));
                term.setStartMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_START_MONTH)));
                term.setStartYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_START_YEAR)));
                term.setEndDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_END_DAY)));
                term.setEndMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_END_MONTH)));
                term.setEndYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_END_YEAR)));
            }
        }
    return term;
    }
    public List<Term> getTerms() {
        List<Term> terms = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.TABLE_TERMS, TERMS_ALL_COLUMNS, null, null, null, null, null);

        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                Term term = new Term();
                term.setTermTitle(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE)));
                term.setStartDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_START_DAY)));
                term.setStartMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_START_MONTH)));
                term.setStartYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_START_YEAR)));
                term.setEndDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_END_DAY)));
                term.setEndMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_END_MONTH)));
                term.setEndYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_END_YEAR)));
                terms.add(term);
            }
        }
        return terms;
    }
    public List<Course> getCourses(Term term) {
        List<Course> courses = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.TABLE_COURSES, COURSES_ALL_COLUMNS, null /*DBOpenHelper.COURSE_TERM + " = \""+term.getTermTitle()+"\""*/, null, null, null, null);
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                Course course = new Course();
                course.setTermTitle(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TERM)));
                course.setCourseTitle(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE)));
                course.setStartDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_START_DAY)));
                course.setStartMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_START_MONTH)));
                course.setStartYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_START_YEAR)));
                course.setEndDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_END_DAY)));
                course.setEndMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_END_MONTH)));
                course.setEndYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_END_YEAR)));
                course.setCourseStatus(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS)));
                courses.add(course);
            }
        }
        return courses;
    }
}
