package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class TermEditor extends AppCompatActivity {

    private String action;
    private ListView list;
    private ArrayAdapter<Course> courseListAdapter;
    private List<Course> courseList;
    private int EDITOR_REQUEST_CODE = 6661;
    String termTitle;
    Term thisTerm = new Term();
    String term;
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

        final TextView startDateEditText = (TextView) findViewById(R.id.startDateEditText);
        final TextView endDateEditText = (TextView) findViewById(R.id.endDateEditText);
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        list = (ListView) findViewById(R.id.courseList);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String id = (String) bundle.get("termTitle");



        if(id == null) {
            action = Intent.ACTION_INSERT;
             }
        else {
            DBProvider provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisTerm = provider.getTerm(id);
            termInputName.setText(thisTerm.getTermTitle());
            startDateEditText.setText(thisTerm.getStartMonth()+1+ "/"+thisTerm.getStartDay()+"/"+thisTerm.getStartYear());
            endDateEditText.setText(thisTerm.getEndMonth()+1+ "/"+thisTerm.getEndDay()+"/"+thisTerm.getEndYear());
        }

        startDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        startDateEditText.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisTerm.setStartDate(selectedDay, selectedMonth+1, selectedYear);
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
                datePicker.setTitle("Select term start date");
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
                        endDateEditText.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisTerm.setEndDate(selectedDay, selectedMonth+1, selectedYear);
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
                datePicker.setTitle("Select term ending date");
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
        displayCourses();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermEditor.this, CourseEditor.class);
                Course course = (Course) parent.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", termInputName.getText().toString().trim());
                bundle.putString("courseTitle", course.getCourseTitle());
                intent.putExtras(bundle);
//                intent.putExtra(DBProvider.CONTENT_ITEM_TYPE, term.getTermTitle());
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
    }

    private void finishedEditing() {
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        switch(action) {
            case Intent.ACTION_INSERT:
        insertTerm(termInputName.getText().toString());
                break;
            case Intent.ACTION_EDIT:
                DBProvider provider = new DBProvider(this);
                provider.open();
                term=thisTerm.getTermTitle();
                thisTerm.setTermTitle(termInputName.getText().toString());
                provider.update(term, thisTerm);
                setResult(RESULT_OK);
                provider.close();
                break;
        }
        finish();
    }


    private void insertTerm(String termTitle) {
        DBProvider provider = new DBProvider(this);
        thisTerm.setTermTitle(termTitle);
        TermHandler.addTerm(thisTerm);
        provider.open();
        provider.add(thisTerm);
        provider.close();
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

    private void displayCourses() {
        DBProvider provider = new DBProvider(this);
        provider.open();
        courseList = provider.getCourses(thisTerm);
        courseListAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, R.id.tvNote, courseList);
        list.setAdapter(courseListAdapter);
        provider.close();
    }

}
