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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AssessmentList extends AppCompatActivity {
    private int EDITOR_REQUEST_CODE = 6661;
    private DBProvider provider;
    private Term thisTerm;
    private Course thisCourse;
    String termTitle;
    String id;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if((savedInstanceState != null)) {
            termTitle = (String) savedInstanceState.get("termTitle");
            id = (String) savedInstanceState.get("courseTitle");
                   }
        else if(bundle != null) {
            termTitle = (String) bundle.getString("termTitle");
            id = (String) bundle.getString("courseTitle");
        }
        else {
            termTitle="";
            id="";
        }
        TextView tvTerm = (TextView) findViewById(R.id.tvTerm1);
        TextView tvCourse = (TextView) findViewById(R.id.tvCourse1);
        tvCourse.setText(id);
        Toast.makeText(this, "set tvCourse", Toast.LENGTH_LONG).show();
        tvTerm.setText(termTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = (ListView) findViewById(R.id.lvAssessments);
        provider = new DBProvider(this);
        provider.open();
        thisCourse = provider.getCourse(id);
        thisTerm = provider.getTerm(termTitle);
        provider.close();
        displayAssessments();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentList.this, AssessmentDetail.class);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", thisTerm.getTermTitle());
                bundle.putString("courseTitle", thisCourse.getCourseTitle());
                intent.putExtras(bundle);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentList.this, AssessmentDetail.class);
                Assessment assessment = (Assessment) parent.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("assessmentId", assessment.getId());
                bundle.putString("courseTitle", thisCourse.getCourseTitle());
                bundle.putString("termTitle", thisTerm.getTermTitle());
                intent.putExtras(bundle);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

    }
    private void displayAssessments() {

        DBProvider provider = new DBProvider(this);
        provider.open();
        List<Assessment> assessmentList = provider.getAssessments(thisCourse);
        ArrayAdapter<Assessment> assessmentListAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, R.id.tvNote1, assessmentList);
        list.setAdapter(assessmentListAdapter);
        provider.close();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        termTitle = (String) savedInstanceState.get("termTitle");
        id = (String) savedInstanceState.get("courseTitle");
        }
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("termTitle", termTitle);
        bundle.putString("courseTitle", id);
        }
}
