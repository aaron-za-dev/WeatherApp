package com.aaronzadev.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aaronzadev.weatherapp.pojo.DummyObject;

public class DetailActivity extends AppCompatActivity {

    //private DummyObject curObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DummyObject curObject = new DummyObject(getIntent().getStringExtra("ItmTitle"),
                getIntent().getStringExtra("ItmDesc"));

        TextView txtDetTitle = findViewById(R.id.txtDetTitle);
        TextView txtDetDesc = findViewById(R.id.txtDetDesc);

        txtDetTitle.setText(curObject.getTitle());
        txtDetDesc.setText(curObject.getDescription());

    }

}
