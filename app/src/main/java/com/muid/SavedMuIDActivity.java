package com.muid;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    Button deleteButton;

    public static final String RESULT_INTENT = "ReceiveResult";
    public static final String COVERART_INTENT = "URL";
    public static final String  LYRICS_INTENT = "LYRICS";
    private String result="", URL="" , lyrics ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_mu_id);

        musicListView = (ListView) findViewById(R.id.musicsList);
        deleteButton = (Button) findViewById(R.id.deleteButton);

//        LayoutInflater inflater = LayoutInflater.from(this);

//        View mButton  = inflater.inflate(R.layout.deletebtnlayout, null);
//        musicListView.addFooterView(mButton);


        musicStringList = new ArrayList<String>();
        URLs = new ArrayList<String>();
        adapter = new ResultsArrayAdapter(getApplicationContext(), musicStringList,URLs);
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
                result = savedMusic.getResult();
                URL = savedMusic.getUrl();
                lyrics = savedMusic.getLyrics();

                intent.putExtra(RESULT_INTENT, result);
                intent.putExtra(COVERART_INTENT, URL);
                intent.putExtra(LYRICS_INTENT, lyrics);

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

    public void showSavedMusic(){
        musicList = musicDao.getAll();

        for(int i=0; i < musicList.size(); i++){
            musicStringList.add(musicDao.getItemById(musicList.get(i).getMuID()).getResult());
            URLs.add(musicDao.getItemById(musicList.get(i).getMuID()).getUrl());
            adapter.notifyDataSetChanged();
        }
    }

}
