package com.muid;

import android.content.Context;
//import android.media.MediaPlayer;
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

//    private TextView mVolume, mResult, tv_time;
//    protected ImageView coverImageView;

    private boolean mProcessing = false;
    //    private boolean mAutoRecognizing = false;
    private boolean initState = false;

//    private MediaPlayer mediaPlayer = new MediaPlayer();
//    private boolean isPlaying = false;

    private String path = "";

    static String applicationID;

    private long startTime = 0;
//    private long stopTime = 0;

    static String title, artist, album, lyrics, URL;
    static long trackID = -1;

    static DeezerConnect deezerConnect;

//    private final int PRINT_MSG = 1001;

    private ACRCloudConfig mConfig = null;
    private ACRCloudClient mClient = null;


    static LyricsClient client = new LyricsClient(); //default is A-Z Lyrics, can be changed to Genius, MusixMatch or LyricsFreak
    static String[] lyricsSources = {"Genius", "A-Z Lyrics", "MusixMatch", "MusicMatch", "LyricsFreak"};
    private Context context;
    private double recordedVolume = 0;

    public double getRecordedVolume() {
        return recordedVolume;
    }

    //    ValuesChangedCallback callback;
    private MainActivity activity;
    private String resultString;

    public MuID(Context C,/* TextView mVolume, TextView mResult, TextView tv_time, ImageView coverImView,*/MainActivity activity) {

        //For saving previous searches
        path = Environment.getExternalStorageDirectory().toString()
                + "/muid";
        Log.e(TAG, path);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.context = C;
//        this.mVolume = mVolume;
//        this.mResult = mResult;
//        this.tv_time = tv_time;
//        this.coverImageView = coverImView;
        this.activity=activity;

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

    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(context, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
//            mVolume.setText("");
            activity.volumeChanged("");
//            mResult.setText("");
//            activity.resultChanged("");
            resultString="";
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
//                mResult.setText("start error!");
//                activity.resultChanged("start error!");
                resultString= "start error!";
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
//        tv_time.setText("");
//        activity.tv_timeChanged("");
//        mResult.setText("");
//        activity.resultChanged("");
        resultString = "";
        trackID = -1;
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
            if (j2 == 0) {
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");

                    //Splitting music information
                    JSONObject tt = (JSONObject) musics.get(0);


                    //Music Title
                    title = tt.getString("title");
                    tres = "Title: " + title + "\n";

                    //Artists names
                    JSONArray artists = tt.getJSONArray("artists");
                    JSONObject art = (JSONObject) artists.get(0);
                    artist = art.getString("name");
                    tres = tres + "Artist: " + artist;
                    //If number of artists > 1
                    for (int i = 1; i < artists.length(); i++) {
                        art = (JSONObject) artists.get(i);
//                        artist = art.getString("name");
                        tres = tres + " , " + art.getString("name");
                    }
                    tres = tres + "\n";

                    //Album Name
                    JSONObject tempAlbum = tt.getJSONObject("album");
                    album = tempAlbum.getString("name");
                    tres = tres + "Album: " + album + "\n";

                    //Deezer

                    JSONObject external_metadata = tt.getJSONObject("external_metadata");
                    System.out.println("hwa da satr el exception 3ashan msh byla2y deezer  " + external_metadata.getJSONObject("deezer"));
                    JSONObject deezer = external_metadata.getJSONObject("deezer");
                    deezer = deezer.getJSONObject("track");
                    String deezerID = deezer.getString("id");
                    trackID = Long.parseLong(deezerID);
                    System.out.println("DEEZER TRACK ID: " + deezerID);

//                    showCoverPhoto();

                }

                //Recognize Lyrics and show them
                new LyricsAdapter().execute(artist, title);

//                tres = tres + "\n\n" + result + "\n\n";
                tres = tres + "\n\n";

            } else {
                resultString = "No Results Found!";
                activity.noResult(resultString);
//                tres = result;
            }
        } catch (JSONException e) {
            new LyricsAdapter().execute(artist, title);
            tres = tres + "\n\n";
            e.printStackTrace();
        }

//        mResult.setText(tres);
//        activity.resultChanged(tres);
        resultString=tres;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        recordedVolume = volume;
//        mVolume.setText("Volume" + volume + "\n\nTime: " + time + " s");
        activity.volumeChanged("Volume" + volume + "\n\nTime: " + time + " s");
    }


    protected void Destroy() {

//        Log.e("MainActivity", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }

    @Override
    public void onRadioMetadataResult(String s) {
//        mResult.setText(s);
//        activity.resultChanged(s);
        resultString=s;
    }

    public void showCoverPhoto(long coverID) {

        if(coverID == -1){
            System.out.println("EL URL EQUALS " + "");
            activity.coverPhotoChanged("");
        }
        else {
            // the request listener
            RequestListener requestListener = new JsonRequestListener() {

                public void onResult(Object result, Object requestId) {
                    Track track = (Track) result;
                    System.out.println(track);
                    Album coverAlbum = track.getAlbum();
//                String coverURL = coverAlbum.getCoverUrl()+ "?size=xl";
                    String coverURL = coverAlbum.getBigImageUrl();
                    URL = coverURL;
                    System.out.println(coverURL);
//                Picasso.get().load(coverURL).into(coverImageView);
                    activity.coverPhotoChanged(coverURL);
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
            showCoverPhoto(trackID);

            try {
//                Lyrics lyricsRequest = null;
                Lyrics[] lyricsRequest = new Lyrics[5];
                boolean noLyrics = true;
                String request = params[0] + " " + params[1];
                System.out.println("REQUEST: " + request);
//                System.out.println("client: "+client.getLyrics(request, "MusixMatch"));
//                lyricsRequest[0] = client.getLyrics(request, "Genius").get();
//                lyricsRequest[1] = client.getLyrics(request, "A-Z Lyrics").get();
//                lyricsRequest[2] = client.getLyrics(request, "MusixMatch").get();
//                lyricsRequest[3] = client.getLyrics(request, "MusicMatch").get();
//                lyricsRequest[4] = client.getLyrics(request, "LyricsFreak").get();

//                System.out.println("LYRICS REQUEST: "+lyricsRequest);
                for (int i = 0; i < lyricsRequest.length; i++) {
                    lyricsRequest[i] = client.getLyrics(request, lyricsSources[i]).get();
                    if (lyricsRequest[i] != null) {
                        noLyrics = false;
                        lyrics = "Powered by:   " + lyricsRequest[i].getSource() + "\n\n" + lyricsRequest[i].getContent();
                        System.out.println("SOURCE: " + lyricsRequest[i].getSource());
                        System.out.println(lyricsRequest[i].getContent());
                        break;
                    }
                }
                if (noLyrics) {
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
//            mResult.setText(mResult.getText() + lyrics);
//            resultString =resultString /*+lyrics*/;
            activity.receiveLyrics(lyrics);
            activity.showResult(resultString);
            activity.receiveCover(URL);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
