package com.muid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;



public class MainActivity extends AppCompatActivity  {

    private TextView mVolume, mResult, tv_time;
    private ImageView coverImageView;
    private MuID MuID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.muid.R.layout.activity_main);


        mVolume = (TextView) findViewById(com.muid.R.id.volume);
        mResult = (TextView) findViewById(com.muid.R.id.result);
        tv_time = (TextView) findViewById(com.muid.R.id.time);

        coverImageView = (ImageView) findViewById(R.id.coverImageView);

        this.MuID = new MuID(getApplicationContext()/*, mVolume, mResult, tv_time, coverImageView*/,this);

        //Start Listening
        findViewById(com.muid.R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MuID.start();
            }
        });

        //Cancel Listening
        findViewById(com.muid.R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MuID.cancel();
                    }
                });

        verifyPermissions();
        MuID.configArc();

    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };

    public void verifyPermissions() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS[i]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS,
                        REQUEST_EXTERNAL_STORAGE);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MuID.Destroy();
    }


    public void resultChanged(String result) {
        mResult.setText(result);
    }
    public void addToResult(String result) {
        mResult.setText( mResult.getText()+result);
    }
    public void volumeChanged(String result) {
        mVolume.setText(result);
    }
    public void tv_timeChanged(String result) {
        tv_time.setText(result);
    }
    public void coverPhotoChanged(String coverURL) {
        Picasso.get().load(coverURL).into(coverImageView);
    }

}
