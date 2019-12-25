package com.muid;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView mVolume/*, mResult, tv_time*/;
//    private ImageView coverImageView;
    private MuID MuID;
    public static final String RESULT_INTENT = "ReceiveResult";
    public static final String COVERART_INTENT = "URL";
    public static final String  LYRICS_INTENT = "LYRICS";
//    public static final String VOLUME_INTENT = "VOLUME";
//    private String /*result,*/URL="not found" , lyrics ="";
    Song song;
    double volume;

    static MusicRoomDatabase musicRoomDatabase;
    static MusicDao musicDao;

    static ImageView historyButton;
    static Button startButton;

    static Animation myFadeInAnimation;
    static RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.muid.R.layout.activity_main);

//        mVolume = (TextView) findViewById(com.muid.R.id.volume);
//        mResult = (TextView) findViewById(com.muid.R.id.result);
//        tv_time = (TextView) findViewById(com.muid.R.id.time);
//
//        coverImageView = (ImageView) findViewById(R.id.coverImageView);

        song = new Song("", "", "", "not found", "");

        // Displaying custom actionbar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar2);
        getSupportActionBar().setElevation(0);
        assert getSupportActionBar() != null;   //null check
        View view = getSupportActionBar().getCustomView();
//        TextView nameTextView = findViewById(R.id.appTitle);
//        nameTextView.setText("MuID");

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

        startButton = (Button) findViewById(R.id.start);
        myFadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tween);
        rippleBackground = (RippleBackground)findViewById(R.id.content);

        musicRoomDatabase = MusicRoomDatabase.getInstance(getApplicationContext());
        musicDao = musicRoomDatabase.getMusicDao();


        this.MuID = new MuID(getApplicationContext()/*, mVolume, mResult, tv_time, coverImageView*/,this,song);


        historyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SavedMuIDActivity.class);
//                finish();
                startActivity(intent);
            }
        });

        //Start Listening
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rippleBackground.startRippleAnimation();
                MuID.start();
//                startButton.startAnimation(myFadeInAnimation);
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
                        rippleBackground.stopRippleAnimation();
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
    public void showResult(/*String result*/) {
//        myFadeInAnimation.cancel();
//        startButton.clearAnimation();
        rippleBackground.stopRippleAnimation();
//        mResult.setText( mResult.getText()+result);

//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
        Intent intent = new Intent(this, ResultsActivity.class);
//        Bundle bundel = new Bundle();
//        bundel.put
        intent.putExtra(RESULT_INTENT, song);

//        intent.putExtra(RESULT_INTENT, result);
//        intent.putExtra(COVERART_INTENT, URL);
//        intent.putExtra(LYRICS_INTENT, lyrics);

        /////////DATABASE INPUTTTT HANDLING
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
        System.out.println("Database save"+song.coverURL);
        musicDao.insert(music);

//        URL ="";
//        lyrics="";


        startActivity(intent);
        song.reset();
    }
    public void receiveLyrics(String lyrics){
        this.song.lyrics =lyrics;

    }

    public void receiveCover(String coverURL){
        this.song.coverURL =coverURL;

    }

    public void noResult(String result) {
//        myFadeInAnimation.cancel();
//        startButton.clearAnimation();
        rippleBackground.stopRippleAnimation();
        Intent intent = new Intent(this, NoResultsActivity.class);
        song.reset();
        startActivity(intent);
    }
//    public void volumeChanged(String result) {
//        mVolume.setText(result);
////        volume =result;
//        volume= MuID.getRecordedVolume();
//    }

//    public void tv_timeChanged(String result) {
////        tv_time.setText(result);
//    }
    public void coverPhotoChanged(String coverURL) {
//        URL=coverURL;
//        Picasso.get().load(coverURL).into(coverImageView)
    }


}
