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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = prefs.edit();
        final TextView tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        final TextView tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        final EditText courseName = (EditText) findViewById(R.id.courseEditorCourseName);
        TextView tvMentorName = (TextView) findViewById(R.id.mentorName);
        final TextView tvMentorname = (TextView) findViewById(R.id.mentorName);
        final TextView tvMentoremail = (TextView) findViewById(R.id.mentorEmail);
        final TextView tvMentorphone = (TextView) findViewById(R.id.mentorPhone);
        final Spinner spinner = (Spinner) findViewById(R.id.statusSpinner);
        final CheckBox cbStartAlarm = (CheckBox) findViewById(R.id.cbStartAlarm);
        final CheckBox cbEndAlarm = (CheckBox) findViewById(R.id.cbEndAlarm);
        final Button assessmentsButton = (Button) findViewById(R.id.assessmentsButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        thisCourse.setTermTitle(termTitle);

        //setting up the layout, if it's an edited term, we have to populate all of the fields
        //from the database
        if (id == null)
            action = Intent.ACTION_INSERT;
        else {
            DBProvider provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisCourse = provider.getCourse(id);
            boolean hasStartAlarm = prefs.getBoolean(thisCourse.getCourseTitle() + "startAlarm", false);
            boolean hasEndAlarm = prefs.getBoolean(thisCourse.getCourseTitle() + "endAlarm", false);
            Mentor mentor;
            if (hasStartAlarm) {
                cbStartAlarm.setEnabled(false);
                cbStartAlarm.setChecked(true);
            }
            if (hasEndAlarm) {
                cbEndAlarm.setEnabled(false);
                cbEndAlarm.setChecked(true);
            }
            if (thisCourse.getCourseStatus() != null) {
                if (thisCourse.getCourseStatus().equals("In progress")) {
                    spinner.setSelection(0);
                }
                if (thisCourse.getCourseStatus().equals("Completed")) {
                    spinner.setSelection(1);
                }
                if (thisCourse.getCourseStatus().equals("Dropped")) {
                    spinner.setSelection(2);
                }
                if (thisCourse.getCourseStatus().equals("Planned")) {
                    spinner.setSelection(3);
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
        provider.close();
        //setting up FAB to add notes to the course
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

        //here using an android design element to allow the user to choose a date from a calendar
        //view instead of having to type in a date
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
        //button to launch assessments activity
        assessmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishedEditing();
                final EditText courseName = (EditText) findViewById(R.id.courseEditorCourseName);
                Intent intent = new Intent(CourseEditor.this, AssessmentList.class);
                Bundle bundle = new Bundle();
                bundle.putString("termTitle", termTitle);
                bundle.putString("courseTitle", courseName.getText().toString().trim());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        tvMentorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMentor();
            }
        });
        //Here I set up code to notify users at 10am on the day they are scheduled to start and end
        //a course
        cbStartAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvStartDate.getText().toString().equals("0/0/0") && !tvStartDate.getText().toString().equals("Click here to set date") && cbStartAlarm.isEnabled()) {
                    try {
                        SetAlarm(tvStartDate.getText().toString(), courseName.getText().toString(), "start");
                        Toast.makeText(CourseEditor.this, "Notification scheduled for " + courseName.getText().toString(), Toast.LENGTH_SHORT).show();
                        cbStartAlarm.setEnabled(false);
                        editor.putBoolean(courseName.getText().toString() + "startAlarm", true);
                        editor.commit();
                    } catch (Exception e) {
                        System.out.println("FAIL");
                        e.printStackTrace();
                    }
                } else if (tvStartDate.getText().toString().equals("0/0/0") || tvStartDate.getText().toString().equals("Click here to set date")) {
                    Toast.makeText(CourseEditor.this, "You must choose a valid date before setting an alarm", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cbEndAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvEndDate.getText().toString().equals("0/0/0") && !tvEndDate.getText().toString().equals("Click here to set date") && cbEndAlarm.isEnabled()) {
                    try {
                        SetAlarm(tvEndDate.getText().toString(), courseName.getText().toString(), "end");
                        Toast.makeText(CourseEditor.this, "Notification scheduled for " + courseName.getText().toString(), Toast.LENGTH_SHORT).show();
                        cbEndAlarm.setEnabled(false);
                        editor.putBoolean(courseName.getText().toString() + "endAlarm", true);
                        editor.commit();
                    } catch (Exception e) {
                        System.out.println("FAIL");
                        e.printStackTrace();
                    }
                } else if (tvEndDate.getText().toString().equals("0/0/0") || tvEndDate.getText().toString().equals("Click here to set date")) {
                    Toast.makeText(CourseEditor.this, "You must choose a valid date before setting an alarm", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void SetAlarm(String date, final String courseName, final String startOrEnd) throws Exception {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(5, getNotification("Course: " + courseName + " is scheduled to " + startOrEnd + " today!"));

                context.unregisterReceiver(this);
            }
        };

        this.registerReceiver(receiver, new IntentFilter("com.example.mord.myapplication"));

        PendingIntent pintent = PendingIntent.getBroadcast(this, 0, new Intent("com.example.mord.myapplication"), 0);
        AlarmManager manager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        date = date + " 10:00:00 PST";
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
        Date tempDate = df.parse(date);
        long epoch = tempDate.getTime();

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (epoch - System.currentTimeMillis()), pintent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("WGU Course update");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        status = adapterView.getItemAtPosition(i).toString();
        thisCourse.setCourseStatus(status);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //code to add a mentor to the database
    public void addMentor() {
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
        if (!tvMentorname.getText().toString().equals("Click here to set mentor"))
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
        switch (action) {
            case Intent.ACTION_INSERT:
                if (courseName.getText().toString() != null && !courseName.getText().toString().trim().equals(""))
                    insertCourse(courseName.getText().toString().trim());
                break;
            case Intent.ACTION_EDIT:
                DBProvider provider = new DBProvider(this);
                provider.open();
                course = thisCourse.getCourseTitle();
                thisCourse.setCourseTitle(courseName.getText().toString());
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
        finishedEditing();
        Intent intent = new Intent(this, TermEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", thisTerm.getTermTitle());

        intent.putExtras(bundle);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);

    }

    private void deleteWarn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete course?");
        builder.setMessage("Are you sure you want to delete this course?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CourseEditor.this.deleteIt();
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
        Toast.makeText(this, "Course deleted", Toast.LENGTH_LONG).show();
        provider.delete(thisCourse);
        setResult(RESULT_OK);
        provider.close();
        Intent intent = new Intent(this, TermEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", thisTerm.getTermTitle());
        bundle.putString("courseTitle", thisCourse.getCourseTitle());
        intent.putExtras(bundle);
        startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
