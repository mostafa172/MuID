package com.muid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class ResultsActivity extends AppCompatActivity implements LyricsFragment.OnFragmentInteractionListener, result_fragment.OnFragmentInteractionListener {
//    static String result, url, lyrics;

   static Song song;
    FragmentPagerAdapter adapterViewPager;

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
        Intent intent = getIntent();
//        result = intent.getStringExtra(MainActivity.RESULT_INTENT);
//        url = intent.getStringExtra(MainActivity.COVERART_INTENT);
//        lyrics = intent.getStringExtra(MainActivity.LYRICS_INTENT);
       song =(Song) intent.getSerializableExtra(MainActivity.RESULT_INTENT);
       System.out.println("Song contents: "+ song.coverURL);


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
//        bundle.putString(MainActivity.LYRICS_INTENT,lyrics);


//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        result_fragment fragment = new result_fragment();
////        fragment.setArguments(bundle);
//        fragmentTransaction.add(R.id.linearLayout, fragment);
//        fragmentTransaction.commit();

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

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public Bundle getBundle() {
        Bundle bundle = new Bundle();
//        bundle.putString(MainActivity.COVERART_INTENT, url);
////        System.out.println("url get bundle " + url);
//        bundle.putString(MainActivity.RESULT_INTENT, result);
//        bundle.putString(MainActivity.LYRICS_INTENT, lyrics);
        bundle.putString(MainActivity.COVERART_INTENT, song.coverURL);
//        System.out.println("url get bundle " + url);
        bundle.putString(MainActivity.RESULT_INTENT, song.title);
        bundle.putString(MainActivity.LYRICS_INTENT, song.lyrics);
//        url="";
//        lyrics="";
//        result="";
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
//                    System.out.println("ANA F CASE 0: " + url + "\n" + "result");
//                    return result_fragment.newInstance(url, result);
                    return result_fragment.newInstance(song.coverURL, song.artist);

                case 1: // Fragment # 1 - This will show FirstFragment different title
//                    System.out.println("ANA F CASE 1: " + lyrics);
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
