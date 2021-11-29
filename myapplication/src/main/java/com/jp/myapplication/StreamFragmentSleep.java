package com.jp.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class StreamFragmentSleep extends Fragment {
    private static final String TAG = StreamFragmentSleep.class.getName();
    private CardView cardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, viewGroup, false);
        cardView = view.findViewById(R.id.cardview_list_item);

        StreamActivity activity = (StreamActivity) getActivity();
        String myDataFromActivity = activity.getMyData();

        return view;
    }
}