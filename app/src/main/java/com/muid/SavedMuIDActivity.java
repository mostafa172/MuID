package com.muid;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SavedMuIDActivity extends AppCompatActivity {

    static MusicRoomDatabase musicRoomDatabase;
    MusicDao musicDao;
    Music music;

    ArrayAdapter<String> adapter;
    ArrayList<String> musicStringList;
    ArrayList<String> URLs;

    List<Music> musicList;
    ListView musicListView;

    ImageView deleteButton;

    public static final String RESULT_INTENT = "ReceiveResult";
    public static final String COVERART_INTENT = "URL";
    public static final String  LYRICS_INTENT = "LYRICS";
    private String title="", artist="", album="", URL="" , lyrics ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_mu_id);

        // Displaying custom actionbar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        View view = getSupportActionBar().getCustomView();
//        TextView nameTextView = findViewById(R.id.name);
//        nameTextView.setText("Search History");
        Window window = getWindow();
        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        //Set Transparent ActionBar
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        //Set Transparent StatusBar
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.transparentColor));

        musicListView = (ListView) findViewById(R.id.musicsList);
        deleteButton = (ImageView) findViewById(R.id.delete);


//        LayoutInflater inflater = LayoutInflater.from(this);

//        View mButton  = inflater.inflate(R.layout.deletebtnlayout, null);
//        musicListView.addFooterView(mButton);


        musicStringList = new ArrayList<String>();
        URLs = new ArrayList<String>();
        adapter = new ResultsArrayAdapter(getApplicationContext(), musicStringList, URLs);
        musicListView.setAdapter(adapter);
        musicRoomDatabase = MusicRoomDatabase.getInstance(getApplicationContext());
        musicDao = musicRoomDatabase.getMusicDao();

        showSavedMusic();

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /* Parameters
            parent:     The AdapterView where the click happened.
            view:       The view within the AdapterView that was clicked (this will be a view provided by the adapter)
            position:   The position of the view in the adapter.
            id:         The row id of the item that was clicked. */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SavedMuIDActivity.this, ResultsActivity.class);

                Music savedMusic = musicList.get(position);
                title = savedMusic.getTitle();
                artist = savedMusic.getArtist();
                album = savedMusic.getAlbum();
                URL = savedMusic.getUrl();
                lyrics = savedMusic.getLyrics();
                Song song = new Song(title,artist,album,URL,lyrics);
                intent.putExtra(RESULT_INTENT, song);
//                intent.putExtra(COVERART_INTENT, URL);
//                intent.putExtra(LYRICS_INTENT, lyrics);

                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicDao.deleteAll();
                musicStringList.clear();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void showSavedMusic(){
        musicList = musicDao.getAll();

        for(int i=0; i < musicList.size(); i++){
            Music tempMusic = musicDao.getItemById(musicList.get(i).getMuID());
            musicStringList.add(tempMusic.getTitle() + "\n" + tempMusic.getArtist() + "\n" +tempMusic.getAlbum());
            System.out.println("Show saved music: "+musicDao.getItemById(musicList.get(i).getMuID()).getUrl());
            URLs.add(musicDao.getItemById(musicList.get(i).getMuID()).getUrl());
            adapter.notifyDataSetChanged();
        }
    }


}
