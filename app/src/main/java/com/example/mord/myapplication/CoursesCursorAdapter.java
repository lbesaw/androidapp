package com.example.mord.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class CoursesCursorAdapter extends CursorAdapter {
    public CoursesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.course_list_item, parent, false);
            }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String courseTitle = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));

        TextView tv = (TextView) view.findViewById(R.id.tvCourse);
        tv.setText(courseTitle);
    }
}
