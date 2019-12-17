package com.muid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        String result = intent.getStringExtra(MainActivity.RESULT_INTENT);
        String url = intent.getStringExtra(MainActivity.COVERART_INTENT);

        TextView textView = findViewById(R.id.result);
        textView.setText(result);
        ImageView    coverImageView = (ImageView) findViewById(R.id.coverImageView);
        Picasso.get().load(url).into(coverImageView);
    }
}
