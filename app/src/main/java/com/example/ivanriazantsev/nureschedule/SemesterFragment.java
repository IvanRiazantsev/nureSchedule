package com.example.ivanriazantsev.nureschedule;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import adapters.SemesterRecyclerAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.moyuru.timetablelayout.layoutmanager.TimetableLayoutManager;


public class SemesterFragment extends Fragment {

    View view;
    Timer mTimer;
    MyTimerTask mTimerTask;
    public static RecyclerView semesterRecyclerView;
    public static SemesterRecyclerAdapter semesterRecyclerAdapter = new SemesterRecyclerAdapter();
    public static TextView semesterPlaceholder;


    public SemesterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_semester, container, false);

        view.setOnClickListener(v -> {
            MainActivity.bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
            MainActivity.bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        semesterPlaceholder = view.findViewById(R.id.semesterPlaceholder);

        semesterRecyclerView = view.findViewById(R.id.semesterRecyclerView);
        semesterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        semesterRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6, GridLayoutManager.HORIZONTAL, false));



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
