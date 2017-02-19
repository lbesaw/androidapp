package com.example.mord.myapplication;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class CourseEditor extends AppCompatActivity {
private Term thisTerm;
    private static final int EDITOR_REQUEST_CODE = 666;
    private Course thisCourse = new Course();
    private ArrayAdapter<Course> courseListAdapter;
    private List<Course> courseList;
    private DBProvider datasource;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = (ListView) findViewById(R.id.courseCourseList);
        datasource = new DBProvider(this);
        datasource.open();


        Bundle bundle = getIntent().getExtras();
        final String termTitle = (String) bundle.get("termTitle");
        thisTerm = datasource.getTerm(termTitle);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Term title is: "+ termTitle, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addCourse();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadCourses();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void addCourse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.temporary_layout, null);
        builder.setView(dialogView);
        builder.setTitle("Add a course");


//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText courseName = (EditText) dialogView.findViewById(R.id.courseEditorCourseName);
                String cName = courseName.getText().toString();
                thisCourse.setCourseTitle(cName);
                Toast.makeText(getBaseContext(),cName, Toast.LENGTH_LONG).show();
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
    private void loadCourses() {
        courseList = datasource.getCourses(thisTerm);
        courseListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, android.R.id.text1, courseList);
        list.setAdapter(courseListAdapter);
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
