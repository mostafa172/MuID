package com.muid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.*;
import com.acrcloud.rec.utils.ACRCloudLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.musixmatch.lyrics.musiXmatchLyricsConnector;

public class MainActivity extends AppCompatActivity implements IACRCloudListener, IACRCloudRadioMetadataListener {

    private final static String TAG = "MainActivity";

    private TextView mVolume, mResult, tv_time;

    private boolean mProcessing = false;
    private boolean mAutoRecognizing = false;
    private boolean initState = false;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;

    private String path = "";

    private long startTime = 0;
    private long stopTime = 0;

    static String title, artist, album, lyrics;

    private final int PRINT_MSG = 1001;

    private ACRCloudConfig mConfig = null;
    private ACRCloudClient mClient = null;


    static LyricsClient client = new LyricsClient(); //default is A-Z Lyrics, can be changed to Genius, MusixMatch or LyricsFreak
    static String[] lyricsSources = {"Genius", "A-Z Lyrics", "MusixMatch", "MusicMatch", "LyricsFreak"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.muid.R.layout.activity_main);

        //For saving previous searches
        path = Environment.getExternalStorageDirectory().toString()
                + "/muid";
        Log.e(TAG, path);

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        mVolume = (TextView) findViewById(com.muid.R.id.volume);
        mResult = (TextView) findViewById(com.muid.R.id.result);
        tv_time = (TextView) findViewById(com.muid.R.id.time);

        //Start Listening
        findViewById(com.muid.R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                start();
            }
        });

        //Cancel Listening
        findViewById(com.muid.R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        cancel();
                    }
                });

        verifyPermissions();

        this.mConfig = new ACRCloudConfig();

        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this;

        // Please create project in "http://console.acrcloud.cn/service/avr".
        this.mConfig.host = this.getString(R.string.host);
        this.mConfig.accessKey = this.getString(R.string.access_key);
        this.mConfig.accessSecret = this.getString(R.string.access_secret);

        // auto recognize access key
        this.mConfig.hostAuto = "";
        this.mConfig.accessKeyAuto = "";
        this.mConfig.accessSecretAuto = "";

        this.mConfig.recorderConfig.rate = 8000;
        this.mConfig.recorderConfig.channels = 1;

        // If you do not need volume callback, you set it false.
        this.mConfig.recorderConfig.isVolumeCallback = true;

        this.mClient = new ACRCloudClient();
        ACRCloudLogger.setLog(true);

        this.initState = this.mClient.initWithConfig(this.mConfig);

    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            mVolume.setText("");
            mResult.setText("");
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
                mResult.setText("start error!");
            }
            startTime = System.currentTimeMillis();
        }
    }

    public void cancel() {
        if (mProcessing && this.mClient != null) {
            this.mClient.cancel();
        }
        this.reset();
    }

    public void reset() {
        tv_time.setText("");
        mResult.setText("");
        mProcessing = false;
    }

    @Override
    public void onResult(ACRCloudResult results) {
        this.reset();

	// If you want to save the record audio data, you can refer to the following codes.
	/*
	byte[] recordPcm = results.getRecordDataPCM();
        if (recordPcm != null) {
            byte[] recordWav = ACRCloudUtils.pcm2Wav(recordPcm, this.mConfig.recorderConfig.rate, this.mConfig.recorderConfig.channels);
            ACRCloudUtils.createFileWithByte(recordWav, path + "/" + "record.wav");
        }
	*/

        String result = results.getResult();

        String tres = "\n";

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");

                    //For more than one recognition
//                    for(int i=0; i<musics.length(); i++) {
//                        JSONObject tt = (JSONObject) musics.get(i);
//                        title = tt.getString("title");
//                        JSONArray artistt = tt.getJSONArray("artists");
//                        JSONObject art = (JSONObject) artistt.get(0);
//                        artist = art.getString("name");
//                        tres = tres + (i+1) + ".  Title: " + title + "    Artist: " + artist + "\n";
//                    }

                    //Splitting music information
                    JSONObject tt = (JSONObject) musics.get(0);

                    //Music Title
                    title = tt.getString("title");
                    tres = "Title: " + title;

                    //Artists names
                    JSONArray artists = tt.getJSONArray("artists");
                    JSONObject art = (JSONObject) artists.get(0);
                    artist = art.getString("name");
                    tres = tres + "    Artist: " + artist;
                    //If number of artists > 1
                    for(int i=1; i<artists.length(); i++){
                        art = (JSONObject) artists.get(i);
                        artist = art.getString("name");
                        tres = tres + " , " + artist ;
                    }

                    //Album Name
                    JSONObject tempAlbum = tt.getJSONObject("album");
                    album = tempAlbum.getString("name");
                    tres = tres + "    Album: " + album + "\n";

                }

                //Recognize Lyrics and show them
                new LyricsAdapter().execute(artist,title);

                tres = tres + "\n\n" + result + "\n\n";
            }else{
                tres = result;
            }
        } catch (JSONException e) {
            tres = result;
            e.printStackTrace();
        }

        mResult.setText(tres);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        mVolume.setText(getResources().getString(com.muid.R.string.volume) + volume + "\n\nTime: " + time + " s");
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };
    public void verifyPermissions() {
        for (int i=0; i<PERMISSIONS.length; i++) {
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

        Log.e("MainActivity", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }

    @Override
    public void onRadioMetadataResult(String s) {
        mResult.setText(s);
    }


    //Lyrics Class
    private class LyricsAdapter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
//            String tempURL = "http://api.musixmatch.com/ws/1.1/matcher.track.get?apikey=" + getApplicationContext().getString(com.muid.R.string.api_key);
//            if (!params[0].equals("<unknown>")) {
//                tempURL += "&q_track=" + params[0];
//            }
//
//            if (!params[1].equals("<unknown>")) {
//                tempURL += "&q_artist=" + params[1];
//            }
//
//            try{
//                URL url = new URL(tempURL);
//                System.out.println(url);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream is = urlConnection.getInputStream();
//                String tempString = new String(ByteStreams.toByteArray(is));
//                JSONObject jsonObject = new JSONObject(tempString);
//                JSONObject obj = jsonObject.getJSONObject("message");
//
//                if (!obj.getString("body").isEmpty()){
//                    obj = obj.getJSONObject("body").getJSONObject("track");
//                    String trackId = obj.getString("track_id");
//                    urlConnection.disconnect();
//
//                    tempURL = "http://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey=" + getApplicationContext().getString(com.muid.R.string.api_key) + "&track_id=" + trackId;
//                    url = new URL(tempURL);
//                    urlConnection = (HttpURLConnection) url.openConnection();
//                    is = urlConnection.getInputStream();
//                    tempString = new String(ByteStreams.toByteArray(is));
//                    jsonObject = new JSONObject(tempString);
//                    lyrics = jsonObject.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics").getString("lyrics_body");
//                    urlConnection.disconnect();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            try {
//                Lyrics lyricsRequest = null;
                Lyrics[] lyricsRequest = new Lyrics[5];
                boolean noLyrics = true;
                String request = params[0] + " " + params[1];
                System.out.println("REQUEST: "+ request);
//                System.out.println("client: "+client.getLyrics(request, "MusixMatch"));
//                lyricsRequest[0] = client.getLyrics(request, "Genius").get();
//                lyricsRequest[1] = client.getLyrics(request, "A-Z Lyrics").get();
//                lyricsRequest[2] = client.getLyrics(request, "MusixMatch").get();
//                lyricsRequest[3] = client.getLyrics(request, "MusicMatch").get();
//                lyricsRequest[4] = client.getLyrics(request, "LyricsFreak").get();

//                System.out.println("LYRICS REQUEST: "+lyricsRequest);
                for (int i=0; i<lyricsRequest.length;i++){
                    lyricsRequest[i] = client.getLyrics(request, lyricsSources[i]).get();
                    if(lyricsRequest[i] != null){
                        noLyrics = false;
                        lyrics = "Powered by:   " + lyricsRequest[i].getSource() + "\n\n" + lyricsRequest[i].getContent();
                        System.out.println("SOURCE: " + lyricsRequest[i].getSource());
                        System.out.println(lyricsRequest[i].getContent());
                        break;
                    }
                }
                if(noLyrics){
                    lyrics = "Lyrics not found!";
                }
//                if(lyricsRequest != null){
//                    lyrics = lyricsRequest.getContent();
//                    System.out.println("SOURCE: " + lyricsRequest.getSource());
//                    System.out.println(lyricsRequest.getContent());
//                }
//                else
//                    lyrics = "Lyrics not found!";

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mResult.setText(mResult.getText() + lyrics);
            lyrics = "";
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
