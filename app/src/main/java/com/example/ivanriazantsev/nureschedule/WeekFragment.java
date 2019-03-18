package com.example.ivanriazantsev.nureschedule;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Timer;
import java.util.TimerTask;

import adapters.WeekSection;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class WeekFragment extends Fragment {

    View view;
    RecyclerView eventsRecyclerView;
    Timer mTimer;
    MyTimerTask mTimerTask;

    public static RecyclerView weekRecyclerView;
    public static SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();


    public WeekFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week, container, false);

        view.setOnClickListener(onOutsideClickListener);


        weekRecyclerView = view.findViewById(R.id.weekRecyclerView);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();



        return view;
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            MainActivity.bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
            MainActivity.bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private View.OnClickListener onOutsideClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputManager =
                        (InputMethodManager) getContext().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            mTimer = new Timer();
            mTimerTask = new MyTimerTask();
            mTimer.schedule(mTimerTask, 50);
        }
    };


}
