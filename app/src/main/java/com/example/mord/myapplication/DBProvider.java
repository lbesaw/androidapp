package com.example.mord.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBProvider {


    public SQLiteDatabase database;
    private SQLiteOpenHelper dbhelper;

    public static final String[] ASSESSMENTS_ALL_COLUMNS = {DBOpenHelper.ASSESSMENT_ID, DBOpenHelper.ASSESSMENT_NAME, DBOpenHelper.ASSESSMENT_COURSE, DBOpenHelper.ASSESSMENT_TYPE, DBOpenHelper.ASSESSMENT_TEXT_NOTES, DBOpenHelper.ASSESSMENT_PIC_NOTES, DBOpenHelper.ASSESSMENT_DUE_DAY,
            DBOpenHelper.ASSESSMENT_DUE_MONTH, DBOpenHelper.ASSESSMENT_DUE_YEAR};

    public static final String[] COURSES_ALL_COLUMNS = {DBOpenHelper.COURSE_ID, DBOpenHelper.COURSE_TERM, DBOpenHelper.COURSE_TITLE, DBOpenHelper.COURSE_STATUS,
            DBOpenHelper.COURSE_TEXT_NOTES, DBOpenHelper.COURSE_PICTURE_NOTES, DBOpenHelper.COURSE_START_DAY, DBOpenHelper.COURSE_START_MONTH, DBOpenHelper.COURSE_START_YEAR, DBOpenHelper.COURSE_END_DAY,
            DBOpenHelper.COURSE_END_MONTH, DBOpenHelper.COURSE_END_YEAR, DBOpenHelper.COURSE_MENTOR};

    public static final String[] MENTORS_ALL_COLUMNS = {DBOpenHelper.MENTOR_NAME, DBOpenHelper.MENTOR_PHONE, DBOpenHelper.MENTOR_EMAIL};

    public static final String[] TERMS_ALL_COLUMNS = {DBOpenHelper.TERM_ID, DBOpenHelper.TERM_TITLE, DBOpenHelper.TERM_START_DAY, DBOpenHelper.TERM_START_MONTH, DBOpenHelper.TERM_START_YEAR, DBOpenHelper.TERM_END_DAY, DBOpenHelper.TERM_END_MONTH, DBOpenHelper.TERM_END_YEAR};

    public DBProvider(Context context) {
        dbhelper = new DBOpenHelper(context);
    }

    public void open() {
        database = dbhelper.getWritableDatabase();
    }


    public void close() {
        dbhelper.close();
    }

    public void add(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE, assessment.getCourse());
        values.put(DBOpenHelper.ASSESSMENT_DUE_DAY, assessment.getDay());
        values.put(DBOpenHelper.ASSESSMENT_DUE_MONTH, assessment.getMonth());
        values.put(DBOpenHelper.ASSESSMENT_DUE_YEAR, assessment.getYear());
        values.put(DBOpenHelper.ASSESSMENT_TYPE, assessment.getType());
        values.put(DBOpenHelper.ASSESSMENT_TEXT_NOTES, assessment.getNote());
        values.put(DBOpenHelper.ASSESSMENT_ID, assessment.getId());
        values.put(DBOpenHelper.ASSESSMENT_NAME, assessment.getName());
        database.insert(DBOpenHelper.TABLE_ASSESSMENTS, null, values);
    }

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

    public void add(Course course) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM, course.getTermTitle());
        values.put(DBOpenHelper.COURSE_TITLE, course.getCourseTitle());
        values.put(DBOpenHelper.COURSE_START_DAY, course.getStartDay());
        values.put(DBOpenHelper.COURSE_START_MONTH, course.getStartMonth());
        values.put(DBOpenHelper.COURSE_START_YEAR, course.getStartYear());
        values.put(DBOpenHelper.COURSE_END_DAY, course.getEndDay());
        values.put(DBOpenHelper.COURSE_END_MONTH, course.getEndMonth());
        values.put(DBOpenHelper.COURSE_END_YEAR, course.getEndYear());
        values.put(DBOpenHelper.COURSE_STATUS, course.getCourseStatus());
        values.put(DBOpenHelper.COURSE_MENTOR, course.getCourseMentor());
        values.put(DBOpenHelper.COURSE_TEXT_NOTES, course.getNotes());
        values.put(DBOpenHelper.COURSE_STATUS, course.getCourseStatus());
        database.insert(DBOpenHelper.TABLE_COURSES, null, values);
    }

    public void add(Mentor mentor) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MENTOR_NAME, mentor.getName());
        values.put(DBOpenHelper.MENTOR_EMAIL, mentor.getEmail());
        values.put(DBOpenHelper.MENTOR_PHONE, mentor.getPhone());
        database.insert(DBOpenHelper.TABLE_MENTORS, null, values);
    }

    public void update(String oldAssessment, Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE, assessment.getCourse());
        values.put(DBOpenHelper.ASSESSMENT_DUE_DAY, assessment.getDay());
        values.put(DBOpenHelper.ASSESSMENT_DUE_MONTH, assessment.getMonth());
        values.put(DBOpenHelper.ASSESSMENT_DUE_YEAR, assessment.getYear());
        values.put(DBOpenHelper.ASSESSMENT_TYPE, assessment.getType());
        values.put(DBOpenHelper.ASSESSMENT_TEXT_NOTES, assessment.getNote());
        values.put(DBOpenHelper.ASSESSMENT_NAME, assessment.getName());
        database.update(DBOpenHelper.TABLE_ASSESSMENTS, values, DBOpenHelper.ASSESSMENT_ID + "='" + oldAssessment + "'", null);
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
        database.update(DBOpenHelper.TABLE_TERMS, values, DBOpenHelper.TERM_TITLE + "=\"" + oldTerm + "\"", null);
    }

    public void update(String oldCourse, Course course) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM, course.getTermTitle());
        values.put(DBOpenHelper.COURSE_TITLE, course.getCourseTitle());
        values.put(DBOpenHelper.COURSE_START_DAY, course.getStartDay());
        values.put(DBOpenHelper.COURSE_START_MONTH, course.getStartMonth());
        values.put(DBOpenHelper.COURSE_START_YEAR, course.getStartYear());
        values.put(DBOpenHelper.COURSE_END_DAY, course.getEndDay());
        values.put(DBOpenHelper.COURSE_END_MONTH, course.getEndMonth());
        values.put(DBOpenHelper.COURSE_END_YEAR, course.getEndYear());
        values.put(DBOpenHelper.COURSE_STATUS, course.getCourseStatus());
        values.put(DBOpenHelper.COURSE_MENTOR, course.getCourseMentor());
        values.put(DBOpenHelper.COURSE_TEXT_NOTES, course.getNotes());
        values.put(DBOpenHelper.COURSE_STATUS, course.getCourseStatus());
        database.update(DBOpenHelper.TABLE_COURSES, values, DBOpenHelper.COURSE_TITLE + "='" + oldCourse + "'", null);
    }

    public Assessment getAssessment(String assName) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_ASSESSMENTS, ASSESSMENTS_ALL_COLUMNS, DBOpenHelper.ASSESSMENT_ID + " = ?", new String[]{assName}, null, null, null);
        Assessment assessment = new Assessment();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                assessment.setCourse(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE)));
                assessment.setDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_DAY)));
                assessment.setMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_MONTH)));
                assessment.setYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_YEAR)));
                assessment.setType(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE)));
                assessment.setNote(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TEXT_NOTES)));
                assessment.setId(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ID)));
                assessment.setName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME)));
            }
            return assessment;
        } else return null;
    }

    public Term getTerm(String termName) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_TERMS, TERMS_ALL_COLUMNS, DBOpenHelper.TERM_TITLE + " = ?", new String[]{termName}, null, null, null);
        Term term = new Term();

        if (cursor.getCount() > 0) {
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

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
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

    public Course getCourse(String id) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_COURSES, COURSES_ALL_COLUMNS, DBOpenHelper.COURSE_TITLE + " like '" + id + "'", null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
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
            course.setCourseMentor(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR)));
            course.setNotes(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT_NOTES)));
            return course;
        }
        return null;
    }

    public Mentor getMentor(Course course) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_MENTORS, MENTORS_ALL_COLUMNS, DBOpenHelper.MENTOR_NAME + " like '" + course.getCourseMentor() + "'", null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Mentor mentor = new Mentor(cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE)));
            return mentor;
        }
        return null;
    }

    public List<Course> getCourses(Term term) {
        List<Course> courses = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.TABLE_COURSES, COURSES_ALL_COLUMNS, DBOpenHelper.COURSE_TERM + " like '" + term.getTermTitle() + "'", null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
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
                course.setNotes(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT_NOTES)));
                course.setCourseMentor(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR)));
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Assessment> getAssessments(Course course) {
        List<Assessment> assessments = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.TABLE_ASSESSMENTS, ASSESSMENTS_ALL_COLUMNS, DBOpenHelper.ASSESSMENT_COURSE + " like '" + course.getCourseTitle() + "'", null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Assessment assessment = new Assessment();
                assessment.setName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME)));
                assessment.setCourse(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE)));
                assessment.setDay(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_DAY)));
                assessment.setMonth(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_MONTH)));
                assessment.setYear(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_YEAR)));
                assessment.setType(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE)));
                assessment.setNote(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TEXT_NOTES)));
                assessment.setId(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ID)));
                assessments.add(assessment);
            }
        }
        return assessments;
    }

    public void delete(Assessment assessment) {
        database.delete(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENT_ID + "='" + assessment.getId() + "'", null);
    }

    public void delete(Term term) {
        database.delete(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERM_TITLE + "='" + term.getTermTitle() + "'", null);
    }

    public void delete(Course course) {
        database.delete(DBOpenHelper.TABLE_COURSES, DBOpenHelper.COURSE_TITLE + "='" + course.getCourseTitle() + "'", null);
    }

    public boolean isTermEmpty(Term term) {
        if (getCourses(term).isEmpty()) {
            return true;
        } else return false;
    }
}
