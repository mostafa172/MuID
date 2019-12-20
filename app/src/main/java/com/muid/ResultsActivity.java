package com.muid;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ResultsActivity extends FragmentActivity implements result_fragment.OnFragmentInteractionListener {
    String result, url, Lyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On create activity results");
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        result = intent.getStringExtra(MainActivity.RESULT_INTENT);
        url = intent.getStringExtra(MainActivity.COVERART_INTENT);
        Lyrics = intent.getStringExtra(MainActivity.LYRICS_INTENT);


//        System.out.println("url in Results activity " + url);
//        System.out.println("Result in results activity" + result);

//        TextView textView = findViewById(R.id.result);
//        textView.setText(result);
//        ImageView    coverImageView = (ImageView) findViewById(R.id.coverImageView);
//        if(url.equals(""))
//            coverImageView.setImageResource(R.drawable.no_cover_found);
//        else
//            Picasso.get().load(url).into(coverImageView);
//        Bundle bundle = new Bundle();
//        bundle.putString(MainActivity.COVERART_INTENT,url);
//        bundle.putString(MainActivity.RESULT_INTENT,result);
//        bundle.putString(MainActivity.LYRICS_INTENT,Lyrics);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        result_fragment fragment = new result_fragment();
//        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.constraintLayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof result_fragment) {
            result_fragment rfragment = (result_fragment) fragment;
            rfragment.setmListener(this);
        }
    }

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public Bundle getBundel() {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.COVERART_INTENT, url);
//        System.out.println("url get bundle " + url);
        bundle.putString(MainActivity.RESULT_INTENT, result);
        bundle.putString(MainActivity.LYRICS_INTENT, Lyrics);
        url="";
        Lyrics="";
        result="";
        return bundle;
    }
}
