package com.example.ivanriazantsev.nureschedule;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapters.SavedGroupsRecyclerViewAdapter;
import adapters.SemesterRecyclerAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;


public class SemesterFragment extends Fragment {

    View view;
    Timer mTimer;
    MyTimerTask mTimerTask;
    public static RecyclerView semesterRecyclerView;
    public static SemesterRecyclerAdapter semesterRecyclerAdapter = new SemesterRecyclerAdapter();
    public static TextView semesterPlaceholder;
    public static LinearLayout timeline;
    public static DatePickerBuilder datePickerBuilder;
    private AppDatabase database = App.getDatabase();
    private GroupDAO groupDAO = database.groupDAO();
    private EventDAO eventDAO = database.eventDAO();

    public static FloatingActionButton backToTodayFAB;

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
        timeline = view.findViewById(R.id.timeline);
        backToTodayFAB = view.findViewById(R.id.backToTodayFAB);

        semesterRecyclerView = view.findViewById(R.id.semesterRecyclerView);
        semesterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        semesterRecyclerView.setHasFixedSize(true);
//        semesterRecyclerAdapter.setHasStableIds(true);

        backToTodayFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                semesterRecyclerView.scrollToPosition((int) ((new Date().getTime() - SavedGroupsRecyclerViewAdapter.begin.getTime()) / 1000 / 60 / 60 / 24));
            }
        });


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
