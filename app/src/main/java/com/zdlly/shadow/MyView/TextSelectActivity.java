package com.zdlly.shadow.MyView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zdlly.shadow.R;

public class TextSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private MyTextView my_textview;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_select);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(this);
        my_textview = (MyTextView) findViewById(R.id.my_textview);
        my_textview.setText(R.string.test_string);
        my_textview.setCursorVisible(false);
        my_textview.setBackgroundColor(Color.WHITE);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                my_textview.changeText();
                break;
        }
    }
}
