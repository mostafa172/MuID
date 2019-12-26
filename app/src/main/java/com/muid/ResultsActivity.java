package com.muid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity implements LyricsFragment.OnFragmentInteractionListener, result_fragment.OnFragmentInteractionListener {

    static Song song;
    FragmentPagerAdapter adapterViewPager;
    public static final String TITLE_INTENT = "ReceiveTitle";
    public static final String ARTIST_INTENT = "ReceiveArtist";
    public static final String ALBUM_INTENT = "ReceiveAlbum";
    public static final String COVERART_INTENT = "ReceiveURL";
    public static final String LYRICS_INTENT = "ReceiveLyrics";

    static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On create activity results");

        // Displaying custom actionbar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("Result");


        setContentView(R.layout.activity_results);
        intent = getIntent();
       song =(Song) intent.getSerializableExtra(MainActivity.RESULT_INTENT);
       System.out.println("ana fl Song contents: "+ song.coverURL);


        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof result_fragment) {
            result_fragment rfragment = (result_fragment) fragment;
            rfragment.setmListener(this);
        }
    }

    public void onFragmentInteraction(Uri uri) {}

    @Override
    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        song =(Song) intent.getSerializableExtra(MainActivity.RESULT_INTENT);
        bundle.putString(TITLE_INTENT, song.title);
        bundle.putString(ARTIST_INTENT, song.artist);
        bundle.putString(ALBUM_INTENT, song.album);
        System.out.println("ana f creation el bundle: " + song.coverURL);
        bundle.putString(COVERART_INTENT, song.coverURL);
        bundle.putString(LYRICS_INTENT, song.lyrics);
        return bundle;
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    System.out.println("ana fl case 0:" + song.coverURL);
                    return result_fragment.newInstance(song.coverURL, song.title, song.artist, song.album);

                case 1: // Fragment # 1 - This will show FirstFragment different title
                    return LyricsFragment.newInstance(song.lyrics);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Song Information";
            else
                return "Lyrics";
        }

    }


}
