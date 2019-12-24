package com.muid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link result_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link result_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class result_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String url;
    private String title;
    private String artist;
    private String album;
    private String lyrics;

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    private OnFragmentInteractionListener mListener;




    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.

    public result_fragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment result_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static result_fragment newInstance(String param1, String param2) {
        result_fragment fragment = new result_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            System.out.println("On Create");
//            url = getArguments().getString(MainActivity.COVERART_INTENT);
//            result = getArguments().getString(MainActivity.RESULT_INTENT);
//            lyrics = getArguments().getString(MainActivity.lyrics_INTENT);
//        }
//       else System.out.println("On Create Not in if");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_result, container, false);
//        ImageView imageView = (ImageView) getView().findViewById(R.id.foo);
        Bundle bundle = mListener.getBundle();
        System.out.println("On Create Fragment");
        if (bundle != null) {
//            System.out.println("On Create");
//            System.out.println("On Create URL"+ url);

            url = bundle.getString(MainActivity.COVERART_INTENT);
//            System.out.println("On Create URL"+ url);
            title = bundle.getString(ResultsActivity.TITLE_INTENT);
            artist = bundle.getString(ResultsActivity.ARTIST_INTENT);
            album = bundle.getString(ResultsActivity.ALBUM_INTENT);
//            lyrics =bundle.getString(MainActivity.lyrics_INTENT);
        }
//        else System.out.println("On Create Not in if");
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = (TextView) view.findViewById(R.id.result);
        TextView textView2 = (TextView) view.findViewById(R.id.result2);
        TextView textView3 = (TextView) view.findViewById(R.id.result3);

//        textView.setText(result +"\n"+lyrics);
        textView.setText(title);
        textView2.setText(artist);
        textView3.setText(album);
        ImageView coverImageView = (ImageView) view.findViewById(R.id.coverImageView);
        if(this.url.equalsIgnoreCase("not found"))
            coverImageView.setImageResource(R.drawable.no_cover_found);
        else
            Picasso.get().load(url).into(coverImageView);
        url="";
        title="";
        artist="";
        album="";

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        Bundle getBundle();
    }
}
