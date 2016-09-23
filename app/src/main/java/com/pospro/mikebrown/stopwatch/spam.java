package com.pospro.mikebrown.stopwatch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class spam extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    public static spam newInstance(int sectionNumber) {
        spam fragment = new spam();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public spam() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_spam, container, false);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
