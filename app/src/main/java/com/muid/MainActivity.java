package com.muid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MuID MuID;

    public static final String RESULT_INTENT = "ReceiveResult";

    Song song;

    private boolean running = false;

    static MusicRoomDatabase musicRoomDatabase;
    static MusicDao musicDao;

    static ImageView historyButton;
    static ImageButton startButton;

    static RippleBackground rippleBackground;
    static MainActivity myActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.muid.R.layout.activity_main);

        song = new Song("", "", "", "not found", "");

        // Displaying custom actionbar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar2);
        getSupportActionBar().setElevation(0);
        assert getSupportActionBar() != null;   //null check

        Window window = getWindow();
        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        //Set Transparent ActionBar
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        //Set Transparent StatusBar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.transparentColor));

        historyButton = (ImageView) findViewById(R.id.history);

        startButton = (ImageButton) findViewById(R.id.start);
        rippleBackground = (RippleBackground)findViewById(R.id.content);

        musicRoomDatabase = MusicRoomDatabase.getInstance(getApplicationContext());
        musicDao = musicRoomDatabase.getMusicDao();


        this.MuID = new MuID(getApplicationContext(),this, song);

        //Show Search History
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SavedMuIDActivity.class);
                startActivity(intent);
            }
        });

        //Start Listening
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startRecognition();
            }
        });

        //Cancel Listening
        findViewById(com.muid.R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MuID.cancel();
//                        myFadeInAnimation.cancel();
//                        startButton.clearAnimation();
                        running= false;
                        if (rippleBackground.isRippleAnimationRunning()) {
                            rippleBackground.stopRippleAnimation();
                            rippleBackground.setVisibility(View.GONE);
                        }
                    }
                });
        verifyPermissions();
        MuID.configArc();
    }

    public void startRecognition() {
        if (!running) {
            if (isMicAvailable()) {
                running=true;
                MuID.start();
                rippleBackground.startRippleAnimation();
                rippleBackground.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "Mic is used by another application.", Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MuID.cancel();
        rippleBackground.stopRippleAnimation();
        rippleBackground.setVisibility(View.GONE);
    }

    public void showResult() {
        running=false;
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(RESULT_INTENT, song);

        //Database Input Handling
        List<Music> items = musicDao.getAll();

        if(items.size() == 10){
            musicDao.deleteFirstItem();
            items = musicDao.getAll();
            System.out.println("New Items Size: " + items.size());
            for(int i=0; i<items.size();i++){
                musicDao.decrementMuID(items.get(i).getMuID());
            }
        }

        int i;
        for(i = 0 ; i<items.size();i++);

        Music music = new Music(i, song.title, song.artist, song.album, song.coverURL, song.lyrics);
        System.out.println("ana fl Database save"+song.coverURL);
        musicDao.insert(music);

        startActivity(intent);
        song.reset();
    }

    public void receiveLyrics(String lyrics) {
        this.song.lyrics = lyrics;

    }

    public void receiveCover(String coverURL){
        this.song.coverURL =coverURL;

    }

    public void noResult(String result) {
        running=false;
        Intent intent = new Intent(this, NoResultsActivity.class);
        song.reset();
        startActivity(intent);
    }

    public void coverPhotoChanged(String coverURL) { }

    private boolean isMicAvailable(){
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        try{
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED ){
                available = false;
            }

            recorder.startRecording();
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING){
                recorder.stop();
                available = false;
            }
            recorder.stop();
        } finally{
            recorder.release();
            recorder = null;
        }

        return available;
    }

}
