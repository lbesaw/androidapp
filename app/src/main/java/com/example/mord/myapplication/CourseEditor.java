package com.example.mord.myapplication;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CourseEditor extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
private Term thisTerm;
    private Course thisCourse = new Course();
    private CursorAdapter cursorAdapter;

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cursorAdapter = new CoursesCursorAdapter(this, null, 0);
        ListView list = (ListView) findViewById(R.id.courseList);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);


        Bundle bundle = getIntent().getExtras();
        final String termTitle = (String) bundle.get("termTitle");
        thisTerm = TermHandler.getTerm(termTitle);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Term title is: "+ termTitle, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addTerm();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void addTerm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.temporary_layout, null);
        builder.setView(dialogView);
        builder.setTitle("Title");


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
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.content.CursorLoader(this, DBProvider.TERM_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
    @Override
    public void onBackPressed() {

    }
}
