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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CourseEditor extends AppCompatActivity {
private Term thisTerm;
    private static final int EDITOR_REQUEST_CODE = 666;
    private Course thisCourse = new Course();
    private ArrayAdapter<Course> courseListAdapter;
    private List<Course> courseList;
    private DBProvider provider;
    private ListView list;
    private Mentor mentor;

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
        thisTerm = provider.getTerm(termTitle);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //// ADD NOTE BUTTON
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final TextView tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        final TextView tvEndDate = (TextView) findViewById(R.id.tvEndDate);

        tvStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        tvStartDate.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisCourse.setStartDate(selectedDay, selectedMonth+1, selectedYear);
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
                        thisCourse.setEndDate(selectedDay, selectedMonth+1, selectedYear);
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

        TextView tvMentorName = (TextView) findViewById(R.id.mentorName);

        tvMentorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMentor();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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


                String mName = mentorName.getText().toString();
                String mEmail = mentorEmail.getText().toString();
                String mPhone = mentorPhone.getText().toString();
                mentor = new Mentor(mName, mEmail, mPhone);
                provider.open();
                provider.add(mentor);
                provider.close();
                thisCourse.setCourseMentor(mName);

                tvMentorname.setText(mName);
                tvMentoremail.setText(mEmail);
                tvMentorphone.setText(mPhone);
                Toast.makeText(CourseEditor.this, "Mentor added!", Toast.LENGTH_SHORT).show();
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

    public void insertCourse(String courseTitle) {
        thisCourse.setCourseTitle(courseTitle);
        DBProvider provider = new DBProvider(this);
        provider.open();
        provider.add(thisCourse);
        provider.close();
        setResult(RESULT_OK);
        Toast.makeText(this, "RESULTS POSTED", Toast.LENGTH_LONG).show();

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
        Toast.makeText(this, "HELLOOOOO", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, TermEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", thisTerm.getTermTitle());
        intent.putExtras(bundle);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }
}
