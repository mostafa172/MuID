package com.muid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class NoResultsActivity extends AppCompatActivity {
    MainActivity main;
    public void tryAgain(View view){
        finish();
        main.startRecognition();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_results);

        // Displaying custom actionbar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("No Result");
        TextView textView = (TextView) findViewById(R.id.noresult);
        textView.setText("NO RESULTS FOUND!");
        main= MainActivity.myActivity;

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
