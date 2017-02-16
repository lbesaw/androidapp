package com.example.mord.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;
    private DBProvider datasource;
    //private ArrayAdapter<Term> termListAdapter;
    private List<Term> termList;
    private ListView list;
    private TermsCursorAdapter termListAdapter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_sample:
                displayTerms();
                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

    }

    private void insertSampleData() {
        insertNote("Simple note");
        insertNote("Multiline\n\nnote");
        insertNote("This is a very long note paseiaojslaih;ihdf;oashdf;oahdf;a as dfhadfhsjdf asjdfh ajs dhfjashdf ");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasource = new DBProvider(this);
        datasource.open();



        list = (ListView) findViewById(R.id.mainList);
        displayTerms();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
//                intent.putExtra(DBProvider.CONTENT_ITEM_TYPE, -1);
//                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });


        Button termButton = (Button) findViewById(R.id.termButton);
        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TermEditor.class);
                startActivity(intent);
            }
        });


    }

    private void displayTerms() {
        termList = datasource.getTerms();
        termListAdapter = new TermsCursorAdapter(this, null, 0);
        //termListAdapter = new ArrayAdapter<Term>(this, R.layout.note_list_item, R.id.tvNote, termList);
        list.setAdapter(termListAdapter);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, noteText);

    }

    public void openEditorForNewNote(View view) {
//
//        Intent intent = new Intent(this, EditorActivity.class);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
        }
    }
}
