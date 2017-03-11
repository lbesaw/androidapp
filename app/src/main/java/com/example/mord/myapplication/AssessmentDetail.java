package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class AssessmentDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String type;
    private String action;
    private String termTitle;
    private String courseTitle;
    private Term term = new Term();
    private Course course = new Course();
    private Assessment thisAss = new Assessment();
    private Spinner spinner;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = (Spinner) findViewById(R.id.assSpinner);
        TextView tvCourseName = (TextView) findViewById(R.id.tvCourseName);
        final TextView dueDate = (TextView) findViewById(R.id.dueDate);
        Bundle bundle = getIntent().getExtras();
        courseTitle = (String) bundle.get("courseTitle");
        id = (String) bundle.get("assessmentTitle");
        termTitle = (String) bundle.get("termTitle");
        DBProvider provider = new DBProvider(this);
        provider.open();
        course = provider.getCourse(courseTitle);
        term = provider.getTerm(termTitle);
        provider.close();
        tvCourseName.setText("Term: "+term.getTermTitle()+ " | Course: "+course.getCourseTitle());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ass_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        if (id == null)
            action = Intent.ACTION_INSERT;
        else {
            provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisAss = provider.getAssessment(id);
            provider.close();
            if (thisAss.getType() != null) {
                if (thisAss.getTypeNo() == 0)
                    spinner.setSelection(0);
                if (thisAss.getTypeNo()== 1)
                    spinner.setSelection(1);
            }
            dueDate.setText(thisAss.getDateAsString());
        }

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
                DatePickerDialog datePicker = new DatePickerDialog(AssessmentDetail.this,
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

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
        type = adapterView.getItemAtPosition(i).toString();
        thisAss.setType(type);
        Toast.makeText(this, thisAss.getType(), Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
}
    @Override
    public void onNothingSelected (AdapterView < ? > adapterView){

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

    private void finishedEditing() {

        DBProvider provider = new DBProvider(this);
        thisAss.setCourse(course.getCourseTitle());
        switch(action) {
            case Intent.ACTION_INSERT:
                provider.open();
                provider.add(thisAss);
                provider.close();
                setResult(RESULT_OK);
                break;
            case Intent.ACTION_EDIT:
                provider.open();
                Toast.makeText(this, "EDIT>>>"+thisAss.getCourse()+thisAss.getType(), Toast.LENGTH_LONG).show();
                provider.update(id, thisAss);
                provider.close();
                setResult(RESULT_OK);
                break;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        finishedEditing();
        Intent intent = new Intent(AssessmentDetail.this, AssessmentList.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", course.getTermTitle());
        bundle.putString("courseTitle", course.getCourseTitle());
        bundle.putString("assessmentTitle", thisAss.getCourse()+thisAss.getType());
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
