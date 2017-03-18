package com.example.mord.myapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ass_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_item_delete:
                deleteWarn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        type = adapterView.getItemAtPosition(i).toString();
        thisAss.setType(type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Setting up initial views used
        spinner = (Spinner) findViewById(R.id.assSpinner);
        TextView tvCourseName = (TextView) findViewById(R.id.tvCourseName);
        final CheckBox cbAssAlarm = (CheckBox) findViewById(R.id.cbAssAlarm);
        final TextView dueDate = (TextView) findViewById(R.id.dueDate);
        //grabbing preferences and preference editor
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = prefs.edit();
        //grabbing values from the bundle passed to this activity
        Bundle bundle = getIntent().getExtras();
        courseTitle = (String) bundle.get("courseTitle");
        id = (String) bundle.get("assessmentId");
        termTitle = (String) bundle.get("termTitle");
        //loading objects to be used from the database
        DBProvider provider = new DBProvider(this);
        provider.open();
        course = provider.getCourse(courseTitle);
        term = provider.getTerm(termTitle);
        provider.close();

        tvCourseName.setText("Term: " + term.getTermTitle() + " | Course: " + course.getCourseTitle());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ass_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        boolean hasAssAlarm = prefs.getBoolean(courseTitle + "assAlarm", false);
        EditText assName = (EditText) findViewById(R.id.assName);
        //here determining if we are editing an existing assessment(and then setting up all of the
        // views) or creating a new one
        if (id == null)
            action = Intent.ACTION_INSERT;
        else {
            provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisAss = provider.getAssessment(id);
            provider.close();
            assName.setText(thisAss.getName());
            if (thisAss.getType() != null) {
                if (thisAss.getTypeNo() == 0)
                    spinner.setSelection(0);
                if (thisAss.getTypeNo() == 1)
                    spinner.setSelection(1);
            }
            if (hasAssAlarm) {
                cbAssAlarm.setEnabled(false);
                cbAssAlarm.setChecked(true);
            }
            dueDate.setText(thisAss.getDateAsString());
        }
        //This is the code to launch a date picker dialog, it displays a calendar so the user
        //doesn't have to type in a date
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
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
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

        //Here, setting the FAB to add notes
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishedEditing();
                Intent intent = new Intent(AssessmentDetail.this, AssessmentNotes.class);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", termTitle);
                bundle.putString("courseTitle", courseTitle);
                bundle.putString("assessmentId", thisAss.getId());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
        //Was running into an android anomaly when using the built in home(or back) button in the
        //tool bar, so disabled it forcing users to press the back button on their device
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        //Here we can set alarms to remind us when to take our test
        cbAssAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dueDate.getText().toString().equals("0/0/0") && !dueDate.getText().toString().equals("Click here to set date") && cbAssAlarm.isEnabled()) {
                    try {
                        SetAlarm(dueDate.getText().toString(), course.getCourseTitle());
                        Toast.makeText(AssessmentDetail.this, "Assessment notification scheduled for " + course, Toast.LENGTH_SHORT).show();
                        cbAssAlarm.setEnabled(false);
                        editor.putBoolean(courseTitle + "assAlarm", true);
                        editor.commit();
                    } catch (Exception e) {
                        System.out.println("FAIL");
                        e.printStackTrace();
                    }
                } else if (dueDate.getText().toString().equals("0/0/0") || dueDate.getText().toString().equals("Click here to set date")) {
                    Toast.makeText(AssessmentDetail.this, "You must choose a valid date before setting an alarm", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Helper class for creating notifications for a future date
    public void SetAlarm(String date, final String courseName) throws Exception {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(10, getNotification("Assessment for " + courseName + " is scheduled for today!"));

                context.unregisterReceiver(this);
            }
        };

        this.registerReceiver(receiver, new IntentFilter("com.example.mord.myapplication"));

        PendingIntent pintent = PendingIntent.getBroadcast(this, 0, new Intent("com.example.mord.myapplication"), 0);
        AlarmManager manager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        date = date + " 10:00:00 PST"; //setting the notification to remind at 10am on the day of the test, a reasonable time
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
        Date tempDate = df.parse(date);
        long epoch = tempDate.getTime();
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (epoch - System.currentTimeMillis()), pintent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("WGU assessment update");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else return null;
    }

    //Display a warning before deleting the assessment
    private void deleteWarn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete assessment?");
        builder.setMessage("Are you sure you want to delete this assessment?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AssessmentDetail.this.deleteIt();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deleteIt() {
        DBProvider provider = new DBProvider(this);
        provider.open();
        Toast.makeText(this, "Assessment deleted", Toast.LENGTH_LONG).show();
        provider.delete(thisAss);
        setResult(RESULT_OK);
        provider.close();
        Intent intent = new Intent(this, CourseEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        bundle.putString("courseTitle", courseTitle);
        bundle.putString("assessmentId", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private void finishedEditing() {
        EditText assName = (EditText) findViewById(R.id.assName);
        DBProvider provider = new DBProvider(this);
        thisAss.setCourse(course.getCourseTitle());
        thisAss.setName(assName.getText().toString().trim());
        switch (action) {
            case Intent.ACTION_INSERT:
                thisAss.setId(thisAss.getCourse() + System.currentTimeMillis());
                provider.open();
                provider.add(thisAss);
                provider.close();
                setResult(RESULT_OK);
                break;
            case Intent.ACTION_EDIT:
                provider.open();
                provider.update(id, thisAss);
                provider.close();
                setResult(RESULT_OK);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finishedEditing();
        Intent intent = new Intent(AssessmentDetail.this, AssessmentList.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        bundle.putString("courseTitle", courseTitle);
        bundle.putString("assessmentId", id);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
