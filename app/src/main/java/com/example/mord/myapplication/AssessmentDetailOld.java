package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

public class AssessmentDetailOld extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String type;
    private String action;
    private String termTitle;
    private String courseTitle;
    private Term term = new Term();
    private Course course = new Course();
    private Assessment thisAss = new Assessment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Spinner spinner = (Spinner) findViewById(R.id.assSpinner);
        final TextView dueDate = (TextView) findViewById(R.id.dueDate);
        Bundle bundle = getIntent().getExtras();
        courseTitle = (String) bundle.get("courseTitle");
        final String id = (String) bundle.get("assessmentTitle");
        termTitle = (String) bundle.get("termTitle");
        DBProvider provider = new DBProvider(this);
        provider.open();
        course = provider.getCourse(courseTitle);
        term = provider.getTerm(termTitle);
        provider.close();

        if (id == null)
            action = Intent.ACTION_INSERT;
        else {
            provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisAss = provider.getAssessment(id);
            provider.close();
            if (thisAss.getType() != null) {
                if (thisAss.getType().equals("Performance assessment"))
                    spinner.setSelection(0);
                if (thisAss.getType().equals("Objective assessment"))
                    spinner.setSelection(1);
            }
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        dueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        dueDate.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisAss.setDueDate(selectedDay, selectedMonth + 1, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

// Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(AssessmentDetailOld.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select assessment due date");
                datePicker.show();

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
        type = adapterView.getItemAtPosition(i).toString();
        thisAss.setType(type);
        //Toast.makeText(this, thisAss.getType(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected (AdapterView < ? > adapterView){

    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(Integer.parseInt(android.os.Build.VERSION.SDK) > 5
//                && keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            onBackPressed();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(AssessmentDetail.this, AssessmentList.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("termTitle", course.getTermTitle());
//        bundle.putString("courseTitle", course.getCourseTitle());
//        intent.putExtras(bundle);
//        startActivity(intent);
//
//    }
}
