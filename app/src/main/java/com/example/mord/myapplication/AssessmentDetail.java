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
import android.widget.ImageView;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = (Spinner) findViewById(R.id.assSpinner);
        TextView tvCourseName = (TextView) findViewById(R.id.tvCourseName);
        final CheckBox cbAssAlarm = (CheckBox) findViewById(R.id.cbAssAlarm);
        final TextView dueDate = (TextView) findViewById(R.id.dueDate);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = prefs.edit();
        Bundle bundle = getIntent().getExtras();
        courseTitle = (String) bundle.get("courseTitle");
        id = (String) bundle.get("assessmentId");
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
        boolean hasAssAlarm = prefs.getBoolean(courseTitle+"assAlarm", false);
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
            if(hasAssAlarm) {
                cbAssAlarm.setEnabled(false);
                cbAssAlarm.setChecked(true);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cbAssAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dueDate.getText().toString().equals("0/0/0") && !dueDate.getText().toString().equals("Click here to set date") && cbAssAlarm.isEnabled()) {
                    try {
                        SetAlarm(dueDate.getText().toString(), course.getCourseTitle());
                        Toast.makeText(AssessmentDetail.this, "Assessment notification scheduled for " + course, Toast.LENGTH_SHORT).show();
                        cbAssAlarm.setEnabled(false);
                        editor.putBoolean(courseTitle+"assAlarm", true);
                        editor.commit();
                    }
                    catch(Exception e){
                        System.out.println("FAIL");
                        e.printStackTrace();
                    }
                }
                else if(dueDate.getText().toString().equals("0/0/0") || dueDate.getText().toString().equals("Click here to set date")) {
                    Toast.makeText(AssessmentDetail.this, "You must choose a valid date before setting an alarm", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void SetAlarm(String date, final String courseName) throws Exception
    {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent )
            {

                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(10, getNotification("Assessment for "+courseName+" is scheduled for today!"));

                context.unregisterReceiver( this );
            }
        };

        this.registerReceiver( receiver, new IntentFilter("com.example.mord.myapplication") );

        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.example.mord.myapplication"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        date = date+" 10:00:00 PST";
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
        Date tempDate = df.parse(date);
        long epoch = tempDate.getTime();

        System.out.println("DEBUG>>> EPOCH LENGTH BETWEEN NOW AND DATE " +(epoch-System.currentTimeMillis()));
        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+(epoch - System.currentTimeMillis()), pintent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("WGU assessment update");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        }
        else return null;
    }


    private void deleteWarn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete note?");
        builder.setMessage("Are you sure you want to delete this note?");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ass_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
        type = adapterView.getItemAtPosition(i).toString();
        thisAss.setType(type);
        Toast.makeText(this, thisAss.getType(), Toast.LENGTH_LONG).show();
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
                thisAss.setId(thisAss.getCourse()+System.currentTimeMillis());
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
       // finish();
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
