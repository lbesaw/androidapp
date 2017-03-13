package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class CourseEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
private Term thisTerm;
    private static final int EDITOR_REQUEST_CODE = 6661;
    private Course thisCourse = new Course();
    private DBProvider provider;
    private Mentor mentor;
    private String action;
    private String course;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        provider = new DBProvider(this);
        provider.open();
        Bundle bundle = getIntent().getExtras();
        final String termTitle = (String) bundle.get("termTitle");
        final String id = (String) bundle.get("courseTitle");
        final TextView tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        final TextView tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        final EditText courseName = (EditText) findViewById(R.id.courseEditorCourseName);
        TextView tvMentorName = (TextView) findViewById(R.id.mentorName);
        final TextView tvMentorname = (TextView) findViewById(R.id.mentorName);
        final TextView tvMentoremail = (TextView) findViewById(R.id.mentorEmail);
        final TextView tvMentorphone = (TextView) findViewById(R.id.mentorPhone);
        final Spinner spinner = (Spinner) findViewById(R.id.statusSpinner);
        final Button assessmentsButton = (Button) findViewById(R.id.assessmentsButton);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        thisCourse.setTermTitle(termTitle);
        if (id == null)
            action = Intent.ACTION_INSERT;
        else {
            DBProvider provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisCourse = provider.getCourse(id);
            Mentor mentor;
            if(thisCourse.getCourseStatus()!=null) {
                if(thisCourse.getCourseStatus().equals("In progress")) {
                    spinner.setSelection(0);
                    Toast.makeText(this, thisCourse.getCourseStatus(), Toast.LENGTH_LONG).show();
                }
                if(thisCourse.getCourseStatus().equals("Completed")) {
                    spinner.setSelection(1);
                    Toast.makeText(this, thisCourse.getCourseStatus(), Toast.LENGTH_LONG).show();
                }
                if(thisCourse.getCourseStatus().equals("Dropped")) {
                    spinner.setSelection(2);
                    Toast.makeText(this, thisCourse.getCourseStatus(), Toast.LENGTH_LONG).show();
                }
                if(thisCourse.getCourseStatus().equals("Planned")) {
                    spinner.setSelection(3);
                    Toast.makeText(this, thisCourse.getCourseStatus(), Toast.LENGTH_LONG).show();
                }
            }
            if (thisCourse.getCourseMentor() != null) {
                mentor = provider.getMentor(thisCourse);
                tvMentorname.setText(mentor.getName());
                tvMentoremail.setText(mentor.getEmail());
                tvMentorphone.setText(mentor.getPhone());
            }
            courseName.setText(thisCourse.getCourseTitle());
            tvStartDate.setText(thisCourse.getStartMonth() + "/" + thisCourse.getStartDay() + "/" + thisCourse.getStartYear());
            tvEndDate.setText(thisCourse.getEndMonth() + "/" + thisCourse.getEndDay() + "/" + thisCourse.getEndYear());
        }
        thisTerm = provider.getTerm(termTitle);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishedEditing();
                Intent intent = new Intent(CourseEditor.this, Notes.class);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", thisTerm.getTermTitle());
                bundle.putString("courseTitle", thisCourse.getCourseTitle());

                intent.putExtras(bundle);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tvStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        tvStartDate.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisCourse.setStartDate(selectedDay, selectedMonth + 1, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

// Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(CourseEditor.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select course start date");
                datePicker.show();

            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        tvEndDate.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisCourse.setEndDate(selectedDay, selectedMonth + 1, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

// Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(CourseEditor.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select course end date");
                datePicker.show();

            }
        });

        assessmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CourseEditor.this, AssessmentList.class);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", termTitle);
                bundle.putString("courseTitle", thisCourse.getCourseTitle());
                intent.putExtras(bundle);
                startActivity(intent);
                finishedEditing();
            }
        });
        tvMentorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMentor();
            }
        });



    }


        @Override
        public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
            status = adapterView.getItemAtPosition(i).toString();
            thisCourse.setCourseStatus(status);
            //Toast.makeText(this, thisCourse.getCourseStatus(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected (AdapterView < ? > adapterView){

        }

    public void addMentor(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.temporary_layout, null);
        final EditText mentorName = (EditText) dialogView.findViewById(R.id.etMentorName);
        final EditText mentorEmail = (EditText) dialogView.findViewById(R.id.etMentorEmail);
        final EditText mentorPhone = (EditText) dialogView.findViewById(R.id.etMentorPhone);
        final TextView tvMentorname = (TextView) findViewById(R.id.mentorName);
        final TextView tvMentoremail = (TextView) findViewById(R.id.mentorEmail);
        final TextView tvMentorphone = (TextView) findViewById(R.id.mentorPhone);
        builder.setView(dialogView);
        builder.setTitle("Add mentor");
        if(!tvMentorname.getText().toString().equals("Click here to set mentor"))
                mentorName.setText(tvMentorname.getText().toString());
                mentorEmail.setText(tvMentoremail.getText().toString());
                mentorPhone.setText(tvMentorphone.getText().toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String mName = mentorName.getText().toString().trim();
                String mEmail = mentorEmail.getText().toString().trim();
                String mPhone = mentorPhone.getText().toString().trim();
                mentor = new Mentor(mName, mEmail, mPhone);
                provider.open();
                provider.add(mentor);
                provider.close();


                tvMentorname.setText(mName);
                tvMentoremail.setText(mEmail);
                tvMentorphone.setText(mPhone);
                thisCourse.setCourseMentor(mName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }

    private void finishedEditing() {
        final EditText courseName = (EditText) findViewById(R.id.courseEditorCourseName);
        thisCourse.setTermTitle(thisTerm.getTermTitle());
        switch(action) {
            case Intent.ACTION_INSERT:
                insertCourse(courseName.getText().toString().trim());
                break;
            case Intent.ACTION_EDIT:
                DBProvider provider = new DBProvider(this);
                provider.open();
                course=thisCourse.getCourseTitle();
                thisCourse.setCourseTitle(courseName.getText().toString());
                Toast.makeText(this, thisCourse.getCourseMentor(), Toast.LENGTH_LONG).show();
                provider.update(course, thisCourse);
                setResult(RESULT_OK);
                provider.close();
                break;
        }
        finish();
    }

    public void insertCourse(String courseTitle) {
        thisCourse.setCourseTitle(courseTitle);
        DBProvider provider = new DBProvider(this);
        provider.open();
        provider.add(thisCourse);
        provider.close();
        setResult(RESULT_OK);

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
        Intent intent = new Intent(this, TermEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", thisTerm.getTermTitle());

        intent.putExtras(bundle);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);

    }
}
