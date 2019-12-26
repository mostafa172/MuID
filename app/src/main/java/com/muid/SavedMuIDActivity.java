package com.muid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;


public class SavedMuIDActivity extends AppCompatActivity {

    static MusicRoomDatabase musicRoomDatabase;
    MusicDao musicDao;

    ArrayAdapter<String> adapter;
    ArrayList<String> musicStringList;
    ArrayList<String> URLs;

    List<Music> musicList;
    ListView musicListView;

    ImageView deleteButton;

    public static final String RESULT_INTENT = "ReceiveResult";
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

        musicListView = (ListView) findViewById(R.id.musicsList);
        deleteButton = (ImageView) findViewById(R.id.delete);

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
