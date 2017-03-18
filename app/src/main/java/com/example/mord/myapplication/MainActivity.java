package com.example.mord.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 666;
    private DBProvider provider;
    private ArrayAdapter<Term> termListAdapter;
    private List<Term> termList;
    private ListView list;
    private Boolean isSwipe = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        provider = new DBProvider(this);
        provider.open();

        list = (ListView) findViewById(R.id.mainList);
        displayTerms();

        if (provider.getTerms().isEmpty()) {
            TextView tv = (TextView) findViewById(R.id.tvMain);
            tv.setText("Welcome!  Please get started by clicking the floating plus button" +
                    " at the bottom of the screen to add a term!");
        } else {
            TextView tv = (TextView) findViewById(R.id.tvMain);
            tv.setText("Welcome back!");
        }
        provider.close();
        //Code to allow user to detect a swipe and then swipe terms away to delete them
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
                                Log.d("action", "action down x: " + action_down_x);
                                Log.d("action", "action up x: " + action_up_x);
                                Log.d("action", "difference: " + difference);
                                //swipe left or right
                                if (difference > 0) {
                                    //swipe left

                                } else {
                                    //swipe right
                                    Log.d("action", "swipe right");
                                }
                                isSwipe = true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("action", "ACTION_UP - ");
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
                if (isSwipe) {
                    DBProvider provider = new DBProvider(MainActivity.this);
                    provider.open();
                    String termName = ((Term) parent.getAdapter().getItem(position)).getTermTitle();
                    if (provider.isTermEmpty((Term) parent.getAdapter().getItem(position))) {

                        provider.delete((Term) parent.getAdapter().getItem(position));
                        provider.close();
                        Toast.makeText(MainActivity.this, "Term: " + termName + " has been deleted!", Toast.LENGTH_SHORT).show();
                        displayTerms();
                    } else {
                        Toast.makeText(MainActivity.this, "Cannot delete term: " + termName + ", it still has courses!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, TermEditor.class);
                    Term term = (Term) parent.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("termTitle", term.getTermTitle());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            }
        });
    }

    private void displayTerms() {
        DBProvider provider = new DBProvider(this);
        provider.open();
        termList = provider.getTerms();
        termListAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, R.id.tvNote1, termList);
        list.setAdapter(termListAdapter);
        provider.close();
    }

    public void openTermEditor(View view) {
        Intent intent = new Intent(MainActivity.this, TermEditor.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", null);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        displayTerms();
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            displayTerms();
        }
    }
}
