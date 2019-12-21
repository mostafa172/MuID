package com.muid;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SavedMuIDActivity extends AppCompatActivity {

    static MusicRoomDatabase musicRoomDatabase;
    MusicDao musicDao;
    Music music;

    ArrayAdapter<String> adapter;
    ArrayList<String> musicStringList;

    List<Music> musicList;
    ListView musicListView;

    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_mu_id);

        musicListView = (ListView) findViewById(R.id.musicsList);
        deleteButton = (Button) findViewById(R.id.deleteButton);


        musicStringList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, musicStringList);
        musicListView.setAdapter(adapter);

        musicRoomDatabase = MusicRoomDatabase.getInstance(getApplicationContext());
        musicDao = musicRoomDatabase.getMusicDao();

        showSavedMusic();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicDao.deleteAll();
//                showSavedMusic();
                //el hagat msh bttshal gher lma batla3 w badkhol tani
            }
        });

    }

    public void showSavedMusic(){
        musicList = musicDao.getAll();

        for(int i=0; i < musicList.size(); i++){
            musicStringList.add(musicDao.getItemById(musicList.get(i).getMuID()).getResult());
            adapter.notifyDataSetChanged();
        }
    }

}
