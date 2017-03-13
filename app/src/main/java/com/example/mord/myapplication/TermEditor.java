package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    String id;
    Boolean isSwipe = false;
    public void setTermTitle(String termTitle) {
        this.termTitle = termTitle;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView startDateEditText = (TextView) findViewById(R.id.startDateEditText);
        final TextView endDateEditText = (TextView) findViewById(R.id.endDateEditText);
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        list = (ListView) findViewById(R.id.courseList);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
       id = (String) bundle.get("termTitle");



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
            provider.close();
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
        displayCourses();
        list.setOnTouchListener(new View.OnTouchListener() {
            private int action_down_x = 0;
            private int action_up_x = 0;
            private int difference = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        action_down_x = (int) event.getX();
                        isSwipe=false;  //until now
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!isSwipe)
                        {
                            action_up_x = (int) event.getX();
                            difference = action_down_x - action_up_x;
                            if(Math.abs(difference)>50)
                            {
                                Log.d("action","action down x: "+action_down_x);
                                Log.d("action","action up x: "+action_up_x);
                                Log.d("action","difference: "+difference);
                                //swipe left or right
                                if(difference>0){
                                    //swipe left

                                }
                                else{
                                    //swipe right
                                    Log.d("action","swipe right");
                                }
                                isSwipe=true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("action", "ACTION_UP - ");
                        action_down_x = 0;
                        action_up_x = 0;
                        difference = 0;
                        break;
                }
                return false;   //to allow the clicklistener to work after
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isSwipe) {

                    DBProvider provider = new DBProvider(TermEditor.this);
                    provider.open();
                    String courseName = ((Course) parent.getAdapter().getItem(position)).getCourseTitle();
                    provider.delete((Course) parent.getAdapter().getItem(position));
                    provider.close();
                    Toast.makeText(TermEditor.this, "Course: "+courseName+" has been deleted!", Toast.LENGTH_SHORT).show();
                    displayCourses();
                }
                else {
                    Intent intent = new Intent(TermEditor.this, CourseEditor.class);
                    Course course = (Course) parent.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("termTitle", termInputName.getText().toString().trim());
                    bundle.putString("courseTitle", course.getCourseTitle());
                    intent.putExtras(bundle);
//                intent.putExtra(DBProvider.CONTENT_ITEM_TYPE, term.getTermTitle());
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void finishedEditing() {
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        switch(action) {
            case Intent.ACTION_INSERT:
        insertTerm(termInputName.getText().toString());
                break;
            case Intent.ACTION_EDIT:
                thisTerm.setTermTitle(termInputName.getText().toString());
                DBProvider provider = new DBProvider(this);
                provider.open();
                provider.update(id, thisTerm);
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
        Toast.makeText(this, "Term: "+termTitle+" created!", Toast.LENGTH_SHORT).show();
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
        finishedEditing();
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        setTermTitle(termInputName.getText().toString());
        Intent intent = new Intent(this, CourseEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        finishedEditing();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finishedEditing();
            NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
}
    private void displayCourses() {

        DBProvider provider = new DBProvider(this);
        provider.open();
        courseList = provider.getCourses(thisTerm);
        courseListAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, R.id.tvNote1, courseList);
        list.setAdapter(courseListAdapter);
        provider.close();
    }



}