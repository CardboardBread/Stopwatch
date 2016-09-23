package com.pospro.mikebrown.stopwatch;

import android.media.MediaPlayer;
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
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class countdown extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    ArrayList<String> points;
    ArrayList<Long> rawPoints;
    ArrayList<String> split;
    ArrayAdapter<String> adapter;

    View outHandler;
    TextView timerText;
    long targetTime = 0;
    int targetMinute;
    int targetSecond;
    long startTime = 0;
    long elapsed = 0;
    String outTime = "";

    public static countdown newInstance(int sectionNumber) {
        countdown fragment = new countdown();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    Handler countHandler = new Handler();
    Runnable countRunnable = new Runnable() {
        @Override
        public void run() {

            if (targetTime - (System.currentTimeMillis() - startTime) >= 0) {
                timerText.setText(translateTime(targetTime - (System.currentTimeMillis() - startTime)));
                outTime = ((String) timerText.getText());
                countHandler.postDelayed(this, 0);
            } else {
                countHandler.removeCallbacks(countRunnable);
                startTime = System.currentTimeMillis();
                timerText.setText(translateTime(0L));
                endBehaviour();
            }
        }
    };

    public countdown() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        points = new ArrayList<>();
        rawPoints = new ArrayList<>();
        split = new ArrayList<>();

        final View rootView = inflater.inflate(R.layout.fragment_countdown, container, false);
        timerText = (TextView) rootView.findViewById(R.id.countDisplay);
        adapter = new ArrayAdapter<>(rootView.getContext(),R.layout.activity_listview,points);

        ListView lapList = (ListView) rootView.findViewById(R.id.countLapList);
        lapList.setAdapter(adapter);

        Button start = (Button) rootView.findViewById(R.id.countStartButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button start = (Button) v;
                TextView label = (TextView) rootView.findViewById(R.id.countStartButton);
                TextView display = (TextView) rootView.findViewById(R.id.countDisplay);
                TextView pauseLabel = (TextView) rootView.findViewById(R.id.countPauseButton);

                if (targetSecond > 0L || targetMinute > 0L) {
                    if (label.getText().equals(getString(R.string.StopLabel))) {

                        countHandler.removeCallbacks(countRunnable);
                        label.setText(getString(R.string.RestartLabel));
                        display.setText(getString(R.string.TimerLabel));

                    } else {
                        targetTime = targetMinute * 60000 + targetSecond * 1000;
                        System.out.println(targetTime);
                        startTime = System.currentTimeMillis();
                        pauseLabel.setText(getString(R.string.PauseLabel));
                        countHandler.postDelayed(countRunnable, 0);
                        label.setText(getString(R.string.StopLabel));
                        points.clear();
                        split.clear();
                        rawPoints.clear();
                        refreshList();
                    }
                }
            }
        });

        Button pause = (Button) rootView.findViewById(R.id.countPauseButton);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button pause = (Button) v;
                TextView label = (TextView) rootView.findViewById(R.id.countStartButton);
                TextView display = (TextView) rootView.findViewById(R.id.countDisplay);
                TextView pauseLabel = (TextView) rootView.findViewById(R.id.countPauseButton);
                if (display.getText().equals(getString(R.string.TimerLabel))) {
                } else {
                    if (label.getText().equals(getString(R.string.StopLabel))) {

                        elapsed = targetTime - (System.currentTimeMillis() - startTime);
                        System.out.println(elapsed);
                        countHandler.removeCallbacks(countRunnable);
                        label.setText(getString(R.string.RestartLabel));
                        pauseLabel.setText(getString(R.string.UnpauseLabel));

                    } else if (label.getText().equals(getString(R.string.RestartLabel))){

                        startTime = System.currentTimeMillis() - elapsed;
                        countHandler.postDelayed(countRunnable, 0);
                        label.setText(getString(R.string.StopLabel));
                        pauseLabel.setText(getString(R.string.PauseLabel));
                    }
                }
            }
        });

        Button lap = (Button) rootView.findViewById(R.id.countLapButton);
        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button lap = (Button) v;
                TextView label = (TextView) rootView.findViewById(R.id.countStartButton);
                TextView display = (TextView) rootView.findViewById(R.id.countDisplay);
                TextView pauseLabel = (TextView) rootView.findViewById(R.id.countPauseButton);

                if (display.getText().equals(getString(R.string.TimerLabel))) {
                } else {
                    if (pauseLabel.getText().equals(getString(R.string.UnpauseLabel))) {

                        String out = translateTime(elapsed);
                        points.add(points.size() + " : " + out);
                        rawPoints.add(elapsed);

                    } else if (label.getText().equals(getString(R.string.StopLabel))) {

                        long timeIn = targetTime - (System.currentTimeMillis() - startTime);
                        String out = translateTime(timeIn);
                        points.add(points.size() + 1 + " : " + out);
                        rawPoints.add(timeIn);
                    }
                    if (points.size() > 1) {
                        int newPos = rawPoints.size() - 1;
                        long newSplit = Math.abs(rawPoints.get(newPos) - rawPoints.get(newPos - 1));
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

        NumberPicker secondPicker = (NumberPicker) rootView.findViewById(R.id.countSecondPicker);
        initalizePicker(secondPicker, 60);

        NumberPicker minutePicker = (NumberPicker) rootView.findViewById(R.id.countMinutePicker);
        initalizePicker(minutePicker, 99);

        outHandler = rootView;
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        countHandler.removeCallbacks(countRunnable);
        TextView label = (TextView) outHandler.findViewById(R.id.countStartButton);
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

    private void initalizePicker (NumberPicker picker, int size) {

        String[] content = new String[size];

        for(int i = 0; i < content.length; i++) {
            content[i] = Integer.toString(i);
        }

        picker.setMinValue(1);
        picker.setMaxValue(content.length);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(content);
        picker.setValue(1);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (picker == outHandler.findViewById(R.id.countSecondPicker)) {
                    targetSecond = picker.getValue() - 1;
                } else if (picker == outHandler.findViewById(R.id.countMinutePicker)) {
                    targetMinute = picker.getValue() - 1;
                }
            }
        });
    }

    private void endBehaviour () {
        final MediaPlayer media = MediaPlayer.create(outHandler.getContext(), R.raw.earday);
        media.start();
    }
}
