package com.muid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ResultsArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> results;
    private final ArrayList<String> URLs;

    public ResultsArrayAdapter(Context context, ArrayList<String> values, ArrayList<String> URLs) {
        super(context, -1, values);
        this.context = context;
        this.results = values;
        this.URLs=URLs;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if(position%2 ==0){
             rowView = inflater.inflate(R.layout.resultsrow_even, parent, false);

        }
        else{
            rowView = inflater.inflate(R.layout.resultsrow_odd, parent, false);

        }
//        View rowView = inflater.inflate(R.layout.resultsrow_even, parent, false);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
        TextView artistTextView = (TextView) rowView.findViewById(R.id.artistTextView);
        TextView albumTextView = (TextView) rowView.findViewById(R.id.albumTextView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.resultIcon);
        String[] resultsText = results.get(position).split("\n");
        titleTextView.setText(resultsText[0]);
        artistTextView.setText(resultsText[1]);
        albumTextView.setText(resultsText[2]);


        // change the icon for Windows and iPhone
        String s = URLs.get(position);
//        if (s.startsWith("iPhone")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }
        if(s.equalsIgnoreCase("not found"))
            imageView.setImageResource(R.drawable.no_cover_found);
        else
            Picasso.get().load(s).into(imageView);

        return rowView;
    }
}