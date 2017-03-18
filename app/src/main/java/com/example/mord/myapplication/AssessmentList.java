package com.example.mord.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AssessmentList extends AppCompatActivity {
    String termTitle = "";
    String id = "";
    ListView list;
    private int EDITOR_REQUEST_CODE = 6661;
    private DBProvider provider;
    private boolean isSwipe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if ((savedInstanceState != null)) {
            termTitle = (String) savedInstanceState.get("termTitle");
            id = (String) savedInstanceState.get("courseTitle");
        } else if (bundle != null) {
            termTitle = bundle.getString("termTitle");
            id = bundle.getString("courseTitle");
        } else {
            termTitle = "";
            id = "";
        }
        TextView tvTerm = (TextView) findViewById(R.id.tvTerm1);
        TextView tvCourse = (TextView) findViewById(R.id.tvCourse1);
        tvCourse.setText(id);
        tvTerm.setText(termTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = (ListView) findViewById(R.id.lvAssessments);
        provider = new DBProvider(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentList.this, AssessmentDetail.class);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", termTitle);
                bundle.putString("courseTitle", id);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //This code is a helper to determine whether or not the touch was a swipe, if it is, it
        //sets the isSwipe boolean to true, which is used later below as a design to delete an
        //assessment
        list.setOnTouchListener(new View.OnTouchListener() {
            private int action_down_x = 0;
            private int action_up_x = 0;
            private int difference = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        action_down_x = (int) event.getX();
                        isSwipe = false;  //until now
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isSwipe) {
                            action_up_x = (int) event.getX();
                            difference = action_down_x - action_up_x;
                            if (Math.abs(difference) > 30) {
                                isSwipe = true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        action_down_x = 0;
                        action_up_x = 0;
                        difference = 0;
                        break;
                }
                return false;
            }
        });
        //here we determine if it was a swipe (and if so, delete the assessment), or if it was a
        //touch, and in that case to launch the detailed assessment view
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSwipe) {
                    DBProvider provider = new DBProvider(AssessmentList.this);
                    provider.open();
                    provider.delete((Assessment) parent.getAdapter().getItem(position));
                    provider.close();
                    Toast.makeText(AssessmentList.this, "Assessment: " + ((Assessment) parent.getAdapter().getItem(position)).toString() + " deleted!", Toast.LENGTH_SHORT).show();
                    isSwipe = false;
                    displayAssessments();
                } else {
                    Intent intent = new Intent(AssessmentList.this, AssessmentDetail.class);
                    Assessment assessment = (Assessment) parent.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    String assId = assessment.getId();
                    bundle.putString("assessmentId", assId);
                    bundle.putString("courseTitle", AssessmentList.this.id);
                    bundle.putString("termTitle", termTitle);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            }
        });
        displayAssessments();
    }

    //method to refresh the listview
    private void displayAssessments() {

        DBProvider provider = new DBProvider(this);
        provider.open();
        Course course = provider.getCourse(id);
        List<Assessment> assessmentList = provider.getAssessments(course);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AssessmentList.this, CourseEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        bundle.putString("courseTitle", id);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
