package com.muid;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

;import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView mVolume/*, mResult, tv_time*/;
//    private ImageView coverImageView;
    private MuID MuID;
    public static final String RESULT_INTENT = "ReceiveResult";
    public static final String COVERART_INTENT = "URL";
    public static final String  LYRICS_INTENT = "LYRICS";
//    public static final String VOLUME_INTENT = "VOLUME";
    private String /*result,*/URL , lyrics ="";
    double volume;

    static MusicRoomDatabase musicRoomDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.muid.R.layout.activity_main);


        mVolume = (TextView) findViewById(com.muid.R.id.volume);
//        mResult = (TextView) findViewById(com.muid.R.id.result);
//        tv_time = (TextView) findViewById(com.muid.R.id.time);
//
//        coverImageView = (ImageView) findViewById(R.id.coverImageView);

        musicRoomDatabase = MusicRoomDatabase.getInstance(getApplicationContext());



        this.MuID = new MuID(getApplicationContext()/*, mVolume, mResult, tv_time, coverImageView*/,this);


        findViewById(com.muid.R.id.saved).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SavedMuIDActivity.class);
//                finish();
                startActivity(intent);
            }
        });
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


//    public void resultChanged(String result) {
////        mResult.setText(result);
//    }
    public void showResult(String result) {
//        mResult.setText( mResult.getText()+result);

//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(RESULT_INTENT, result);
//        System.out.println("Url main activity"+URL);
        intent.putExtra(COVERART_INTENT, URL);

        intent.putExtra(LYRICS_INTENT, lyrics);

        /////////DATABASE INPUTTTT HANDLING
        MusicDao musicDao = musicRoomDatabase.getMusicDao();

        List<Music> items = musicDao.getAll();
        int i;
        for(i = 0 ; i<items.size();i++)
            System.out.println("DB ITEMS: " + items.get(i).toString());

        Music music = new Music(i, result, URL, lyrics);
        musicDao.insert(music);

        items = musicDao.getAll();

        for(i = 0 ; i<items.size();i++)
            System.out.println("DB ITEMS 2: " + items.get(i).toString());

        URL ="";
        lyrics="";

//        finish();
        startActivity(intent);
    }
    public void receiveLyrics(String lyrics){
        this.lyrics =lyrics;

    }

    public void receiveCover(String coverURL){
        this.URL =coverURL;

    }

    public void noResult(String result) {
        Intent intent = new Intent(this, NoResultsActivity.class);
        finish();
        startActivity(intent);
    }
    public void volumeChanged(String result) {
        mVolume.setText(result);
//        volume =result;
        volume= MuID.getRecordedVolume();
    }
//    public void tv_timeChanged(String result) {
////        tv_time.setText(result);
//    }
    public void coverPhotoChanged(String coverURL) {
        URL=coverURL;
//        Picasso.get().load(coverURL).into(coverImageView)
    }


}
