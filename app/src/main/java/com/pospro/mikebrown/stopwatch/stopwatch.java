package com.pospro.mikebrown.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class stopwatch extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    ArrayList<String> points;
    ArrayList<Long> rawPoints;
    ArrayList<String> split;
    ArrayAdapter<String> adapter;

    View outHandler;
    TextView timerText;
    long startTime = 0;
    long elapsed = 0;
    String outTime = "";

    public static stopwatch newInstance(int sectionNumber) {
        stopwatch fragment = new stopwatch();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            timerText.setText(translateTime(System.currentTimeMillis() - startTime));
            outTime = ((String) timerText.getText());
            timerHandler.postDelayed(this, 0);
        }
    };

    public stopwatch() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        points = new ArrayList<>();
        rawPoints = new ArrayList<>();
        split = new ArrayList<>();

        final View rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        timerText = (TextView) rootView.findViewById(R.id.watchDisplay);
        adapter = new ArrayAdapter<>(rootView.getContext(),R.layout.activity_listview,points);

        ListView lapList = (ListView) rootView.findViewById(R.id.watchLapList);
        lapList.setAdapter(adapter);

        Button start = (Button) rootView.findViewById(R.id.watchStartButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button start = (Button) v;
                TextView label = (TextView) rootView.findViewById(R.id.watchStartButton);
                TextView display = (TextView) rootView.findViewById(R.id.watchDisplay);
                TextView pauseLabel = (TextView) rootView.findViewById(R.id.watchPauseButton);

                if (label.getText().equals(getString(R.string.StopLabel))) {

                    timerHandler.removeCallbacks(timerRunnable);
                    label.setText(getString(R.string.RestartLabel));
                    display.setText(getString(R.string.TimerLabel));

                } else {

                    startTime = System.currentTimeMillis();
                    pauseLabel.setText(getString(R.string.PauseLabel));
                    timerHandler.postDelayed(timerRunnable, 0);
                    label.setText(getString(R.string.StopLabel));
                    points.clear();
                    split.clear();
                    rawPoints.clear();
                    refreshList();
                }

            }
        });

        Button pause = (Button) rootView.findViewById(R.id.watchPauseButton);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button pause = (Button) v;
                TextView label = (TextView) rootView.findViewById(R.id.watchStartButton);
                TextView display = (TextView) rootView.findViewById(R.id.watchDisplay);
                TextView pauseLabel = (TextView) rootView.findViewById(R.id.watchPauseButton);

                if (display.getText().equals(getString(R.string.TimerLabel))) {
                } else {
                    if (label.getText().equals(getString(R.string.StopLabel))) {

                        elapsed = System.currentTimeMillis() - startTime;
                        System.out.println(elapsed);
                        timerHandler.removeCallbacks(timerRunnable);
                        label.setText(getString(R.string.RestartLabel));
                        pauseLabel.setText(getString(R.string.UnpauseLabel));

                    } else if (label.getText().equals(getString(R.string.RestartLabel))){

                        startTime = System.currentTimeMillis() - elapsed;
                        timerHandler.postDelayed(timerRunnable, 0);
                        label.setText(getString(R.string.StopLabel));
                        pauseLabel.setText(getString(R.string.PauseLabel));
                    }
                }
            }
        });

        Button lap = (Button) rootView.findViewById(R.id.watchLapButton);
        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button lap = (Button) v;
                TextView label = (TextView) rootView.findViewById(R.id.watchStartButton);
                TextView display = (TextView) rootView.findViewById(R.id.watchDisplay);
                TextView pauseLabel = (TextView) rootView.findViewById(R.id.watchPauseButton);

                if (display.getText().equals(getString(R.string.TimerLabel))) {
                } else {
                    if (pauseLabel.getText().equals(getString(R.string.UnpauseLabel))) {

                        String out = translateTime(elapsed);
                        points.add(points.size() + " : " + out);
                        rawPoints.add(elapsed);

                    } else if (label.getText().equals(getString(R.string.StopLabel))) {

                        long timeIn = System.currentTimeMillis() - startTime;
                        String out = translateTime(timeIn);
                        points.add(points.size() + 1 + " : " + out);
                        rawPoints.add(timeIn);
                    }
                    if (points.size() > 1) {
                        int newPos = rawPoints.size() - 1;
                        long newSplit = rawPoints.get(newPos) - rawPoints.get(newPos - 1);
                        split.add(translateTime(newSplit));
                    } else {
                        split.add(outTime);
                    }
                }
                refreshList();
            }
        });

        lapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick (AdapterView<?> parent, View viewClicked, int position, long id) {
                Snackbar.make(parent, "Split : " + split.get(position), Snackbar.LENGTH_LONG)
                        .setAction("Split for item " + position, null).show();
            }
        });

        outHandler = rootView;
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        TextView label = (TextView) outHandler.findViewById(R.id.watchStartButton);
        label.setText(getString(R.string.StartLabel));
    }

    private String translateTime (Long time) {

        long milliseconds = time;
        int seconds = (int) (milliseconds / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d:%02d", minutes, seconds, (milliseconds % 1000) / 10 );
    }

    private void refreshList () {

        adapter.notifyDataSetChanged();
    }
}
