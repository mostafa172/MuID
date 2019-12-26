package com.muid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.acrcloud.rec.ACRCloudClient;
import com.acrcloud.rec.ACRCloudConfig;
import com.acrcloud.rec.ACRCloudResult;
import com.acrcloud.rec.IACRCloudListener;
import com.acrcloud.rec.IACRCloudRadioMetadataListener;
import com.acrcloud.rec.utils.ACRCloudLogger;
import com.deezer.sdk.model.Album;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.RequestListener;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class MuID implements IACRCloudListener, IACRCloudRadioMetadataListener {

    private final static String TAG = "MainActivity";

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "";

    static String applicationID;

    private long startTime = 0;

    Song song;
    static long trackID = -1;

    static DeezerConnect deezerConnect;

    private ACRCloudConfig mConfig = null;
    private ACRCloudClient mClient = null;


    static LyricsClient client = new LyricsClient(); //default is A-Z Lyrics, can be changed to Genius, MusixMatch or LyricsFreak
    static String[] lyricsSources = {"Genius", "A-Z Lyrics", "MusixMatch", "MusicMatch", "LyricsFreak"};
    private Context context;
    private double recordedVolume = 0;

    public double getRecordedVolume() {
        return recordedVolume;
    }

    private MainActivity activity;

    public MuID(Context C, MainActivity activity, Song song) {

        //For saving previous searches
        path = Environment.getExternalStorageDirectory().toString()
                + "/muid";
        Log.e(TAG, path);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        this.context = C;
        this.activity=activity;
        this.song=song;

        applicationID = context.getString(R.string.application_id);
        deezerConnect = new DeezerConnect(context, applicationID);
    }

    public void configArc() {
        this.mConfig = new ACRCloudConfig();

        this.mConfig.acrcloudListener = this;
        this.mConfig.context = context;

        // Please create project in "http://console.acrcloud.cn/service/avr".
        this.mConfig.host = context.getString(R.string.host);
        this.mConfig.accessKey = context.getString(R.string.access_key);
        this.mConfig.accessSecret = context.getString(R.string.access_secret);

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
        this.mClient.stopPreRecord();

    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(context, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
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
        trackID = -1;
        mProcessing = false;
    }

    @Override
    public void onResult(ACRCloudResult results) {
        this.reset();

        String result = results.getResult();
        String tres = "\n";
        String tempArtist="";

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if (j2 == 0) {
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");

                    //Splitting music information
                    JSONObject tt = (JSONObject) musics.get(0);

                    //Music Title
                    song.title = tt.getString("title");

                    //Artists names
                    JSONArray artists = tt.getJSONArray("artists");
                    JSONObject art = (JSONObject) artists.get(0);
                    tempArtist = art.getString("name");
                    song.artist = art.getString("name");

                    //If number of artists > 1
                    for (int i = 1; i < artists.length(); i++) {
                        art = (JSONObject) artists.get(i);
                        song.artist = song.artist + " , " + art.getString("name");
                    }

                    //Album Name
                    JSONObject tempAlbum = tt.getJSONObject("album");
                    song.album = tempAlbum.getString("name");

                    //Deezer Track ID
                    JSONObject external_metadata = tt.getJSONObject("external_metadata");
                    System.out.println("hwa da satr el exception 3ashan msh byla2y deezer  " + external_metadata.getJSONObject("deezer"));
                    JSONObject deezer = external_metadata.getJSONObject("deezer");
                    deezer = deezer.getJSONObject("track");
                    String deezerID = deezer.getString("id");
                    trackID = Long.parseLong(deezerID);
                    System.out.println("ana fl DEEZER TRACK ID: " + trackID);
                }

                //Recognize Lyrics and Show Cover
                new LyricsAdapter().execute(tempArtist, song.title, String.valueOf(trackID));

            } else {
                activity.noResult("No Results Found!");
            }

        } catch (JSONException e) {
            new LyricsAdapter().execute(song.artist, song.title, String.valueOf(trackID));
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        recordedVolume = volume;
    }


    protected void Destroy() {
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }

    @Override
    public void onRadioMetadataResult(String s) { }

    public void showCoverPhoto(long coverID) {

        System.out.println("ana fl show coverphoto EL URL EQUALS " + coverID);
        if(coverID == -1){
            System.out.println("ana fl show coverphoto -1 EL URL EQUALS " + coverID);
        }
        else {
            // the request listener
            RequestListener requestListener = new JsonRequestListener() {

                public void onResult(Object result, Object requestId) {
                    Track track = (Track) result;
                    System.out.println(track);
                    Album coverAlbum = track.getAlbum();
                    String coverURL = coverAlbum.getBigImageUrl();
                    System.out.println("ana fl COVER 1: " + coverURL);
                    song.coverURL = coverURL;
                    System.out.println("ana fl COVER 1: " + coverURL);
                }

                public void onUnparsedResult(String requestResponse, Object requestId) {
                }

                public void onException(Exception e, Object requestId) {
                }
            };

            // create the request
            DeezerRequest request = DeezerRequestFactory.requestTrack(coverID);

            // set a requestId, that will be passed on the listener's callback methods
            request.setId("myRequest");

            // launch the request asynchronously
            deezerConnect.requestAsync(request, requestListener);
        }

    }


    //Lyrics Class
    private class LyricsAdapter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            System.out.println("ana fl do in background: " + Long.valueOf(params[2]));
            showCoverPhoto(Long.valueOf(params[2]));

            try {

                Lyrics[] lyricsRequest = new Lyrics[5];
                boolean noLyrics = true;
                String request = params[0] + " " + params[1];
                System.out.println("REQUEST: " + request);

                for (int i = 0; i < lyricsRequest.length; i++) {
                    lyricsRequest[i] = client.getLyrics(request, lyricsSources[i]).get();
                    if (lyricsRequest[i] != null) {
                        noLyrics = false;
                        song.lyrics = "Powered by:   " + lyricsRequest[i].getSource() + "\n\n" + lyricsRequest[i].getContent();

                        System.out.println("SOURCE: " + lyricsRequest[i].getSource());
                        System.out.println(lyricsRequest[i].getContent());
                        break;
                    }
                }
                if (noLyrics) {
                    song.lyrics = "Lyrics not found!";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            activity.showResult();
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onProgressUpdate(Void... values) { }
    }
}
