package com.example.mord.myapplication;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class TermEditor extends AppCompatActivity {

    private String action;
    private ListView list;
    private ArrayAdapter<Course> courseListAdapter;
    private List<Course> courseList;
    private int EDITOR_REQUEST_CODE = 6661;
    String termTitle;
    Term thisTerm = new Term();
    String termId;
    Boolean isSwipe = false;

    public void setTermTitle(String termTitle) {
        this.termTitle = termTitle;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView startDateEditText = (TextView) findViewById(R.id.startDateEditText);
        final TextView endDateEditText = (TextView) findViewById(R.id.endDateEditText);
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        list = (ListView) findViewById(R.id.courseList);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        termId = (String) bundle.get("termTitle");
        //code to set up the layout if this is an edited term
        if (termId == null) {
            action = Intent.ACTION_INSERT;
        } else {
            DBProvider provider = new DBProvider(this);
            provider.open();
            action = Intent.ACTION_EDIT;
            thisTerm = provider.getTerm(termId);
            termInputName.setText(thisTerm.getTermTitle());
            startDateEditText.setText(thisTerm.getStartMonth() + 1 + "/" + thisTerm.getStartDay() + "/" + thisTerm.getStartYear());
            endDateEditText.setText(thisTerm.getEndMonth() + 1 + "/" + thisTerm.getEndDay() + "/" + thisTerm.getEndYear());
            provider.close();
        }
        //calendar picker so user doesnt have to enter a date manually
        startDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        startDateEditText.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisTerm.setStartDate(selectedDay, selectedMonth + 1, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

                DatePickerDialog datePicker = new DatePickerDialog(TermEditor.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select term start date");
                datePicker.show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        endDateEditText.setText(selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear);
                        thisTerm.setEndDate(selectedDay, selectedMonth + 1, selectedYear);
                    }
                };
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                DatePickerDialog datePicker = new DatePickerDialog(TermEditor.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select term ending date");
                datePicker.show();

            }
        });
        displayCourses();
        //code to detect a swipe, and if its a swipe, to remove the course from the term
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
                            if (Math.abs(difference) > 50) {
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSwipe) {    //if its a swipe, delete the course!

                    DBProvider provider = new DBProvider(TermEditor.this);
                    provider.open();
                    String courseName = ((Course) parent.getAdapter().getItem(position)).getCourseTitle();
                    provider.delete((Course) parent.getAdapter().getItem(position));
                    provider.close();
                    Toast.makeText(TermEditor.this, "Course: " + courseName + " has been deleted!", Toast.LENGTH_SHORT).show();
                    displayCourses();
                } else {
                    Intent intent = new Intent(TermEditor.this, CourseEditor.class);
                    Course course = (Course) parent.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("termTitle", termInputName.getText().toString().trim());
                    bundle.putString("courseTitle", course.getCourseTitle());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void finishedEditing() {
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        switch (action) {
            case Intent.ACTION_INSERT:
                if (termInputName.getText().toString() != null && !termInputName.getText().toString().trim().equals(""))
                    insertTerm(termInputName.getText().toString());
                break;
            case Intent.ACTION_EDIT:
                thisTerm.setTermTitle(termInputName.getText().toString());
                DBProvider provider = new DBProvider(this);
                provider.open();
                provider.update(termId, thisTerm);
                setResult(RESULT_OK);
                provider.close();
                break;
        }
        finish();
    }


    private void insertTerm(String termTitle) {
        DBProvider provider = new DBProvider(this);
        thisTerm.setTermTitle(termTitle);
        provider.open();
        provider.add(thisTerm);
        provider.close();
        setResult(RESULT_OK);
        Toast.makeText(this, "Term: " + termTitle + " created!", Toast.LENGTH_SHORT).show();
    }

    public void launchCourseEditor(View view) {
        finishedEditing();
        final EditText termInputName = (EditText) findViewById(R.id.termInputName);
        setTermTitle(termInputName.getText().toString());
        Intent intent = new Intent(this, CourseEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        intent.putExtras(bundle);
        startActivity(intent);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void displayCourses() {

        DBProvider provider = new DBProvider(this);
        provider.open();
        courseList = provider.getCourses(thisTerm);
        courseListAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, R.id.tvNote1, courseList);
        list.setAdapter(courseListAdapter);
        provider.close();
    }

    private void deleteWarn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete term?");
        builder.setMessage("Are you sure you want to delete this term?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DBProvider provider = new DBProvider(TermEditor.this);
                provider.open();
                if (provider.isTermEmpty(thisTerm))
                    TermEditor.this.deleteIt();
                else {
                    Toast.makeText(TermEditor.this, "You may only delete terms containing no courses!", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
                provider.close();
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
        Toast.makeText(this, "Term deleted", Toast.LENGTH_LONG).show();
        provider.delete(thisTerm);
        setResult(RESULT_OK);
        provider.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finishedEditing();
                NavUtils.navigateUpFromSameTask(this);
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
        inflater.inflate(R.menu.menu_term_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }


}