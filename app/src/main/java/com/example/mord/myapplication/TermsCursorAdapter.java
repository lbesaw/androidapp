package com.example.mord.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class TermsCursorAdapter extends CursorAdapter {
    public TermsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item, parent, false);
            }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(noteText);
    }
}