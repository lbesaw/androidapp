package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import javax.sql.DataSource;

public class TermEditor extends AppCompatActivity {
    int startDay, startMonth, startYear, endDay, endMonth, endYear;
    String termTitle;
    Term thisTerm = new Term();

    public void setTermTitle(String termTitle) {
        this.termTitle = termTitle;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText startDateEditText = (EditText) findViewById(R.id.startDateEditText);
        final EditText endDateEditText = (EditText) findViewById(R.id.endDateEditText);

        startDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        startDateEditText.setText("" + selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisTerm.setStartDate(selectedDay, selectedMonth, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

// Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(TermEditor.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();

            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        endDateEditText.setText("" + selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisTerm.setEndDate(selectedDay, selectedMonth, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

// Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(TermEditor.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();

            }
        });
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishedEditing();
            }
        });

    }

    private void finishedEditing() {
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
//        switch(action) {
//            case Intent.ACTION_INSERT:
        insertNote(termInputName.getText().toString(), startDay, startMonth, startYear, endDay, endMonth, endYear);
//                break;
//        }
//        finish();
    }

    /// TO IMPLEMENT UPDATE TERM
//    private void updateNote(String noteText) {
//        ContentValues values = new ContentValues();
//        values.put(DBOpenHelper.TERM_TITLE, noteText);
//        getContentResolver().update(DBProvider.TERM_CONTENT_URI, values, noteFilter, null);
//        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//    }

    private void insertNote(String termTitle, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {
        DBProvider datasource = new DBProvider(this);
        thisTerm.setTermTitle(termTitle);
        thisTerm.setStartDate(startDay, startMonth, startYear);
        thisTerm.setEndDate(endDay, endMonth, endYear);
        TermHandler.addTerm(thisTerm);
        datasource.open();
        datasource.add(thisTerm);
        datasource.close();
        setResult(RESULT_OK);
        Toast.makeText(this, "RESULTS POSTED", Toast.LENGTH_LONG).show();
    }

/// TO IMPLEMENT DELETE TERM
//    private void deleteNote() {
//        getContentResolver().delete(DBProvider.TERM_CONTENT_URI,
//                noteFilter, null);
//        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//        finish();
//    }

    public void launchCourseEditor(View view) {
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        setTermTitle(termInputName.getText().toString());
        Intent intent = new Intent(this, CourseEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finishedEditing();
    }


}
