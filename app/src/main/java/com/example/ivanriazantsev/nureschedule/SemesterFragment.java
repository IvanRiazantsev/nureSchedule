package com.example.ivanriazantsev.nureschedule;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import adapters.SemesterRecyclerAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SemesterFragment extends Fragment {

    View view;
    Timer mTimer;
    MyTimerTask mTimerTask;
    public static RecyclerView semesterRecyclerView;
    public static SemesterRecyclerAdapter semesterRecyclerAdapter = new SemesterRecyclerAdapter();
    public static TextView semesterPlaceholder;
//    public static ProgressBar progressBar;
//    private final long studyPeriodLength = 37800000;
//    private final long studyPeriodStart = 27900000;
//    private final long studyPeriodEnd = 65700000;
//    CountDownTimer countDownTimer;

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

//        progressBar = view.findViewById(R.id.pb);
//        progressBar.setMax((int) studyPeriodLength);

//        Date currentDate = new Date();
//        final long[] currentTime = {currentDate.getTime()};
//        long dayStart = App.getStartOfDay(currentDate).getTime();
//        final long[] timeSinceDayStart = {currentTime[0] - dayStart};
//        final long[] timeSinceStudyPeriodStart = {currentTime[0] - dayStart - studyPeriodStart};
//        long timeToDayEnd = App.getStartOfNextDay(new Date(dayStart)).getTime() - currentTime[0];
//
//        System.out.println(App.getStartOfNextDay(new Date(dayStart)).getTime());
//        System.out.println(timeSinceDayStart[0]);
//        System.out.println(timeToDayEnd);
//        System.out.println(timeSinceStudyPeriodStart[0]);
//
//        if (timeSinceDayStart[0] < studyPeriodStart)
//            progressBar.setProgress(0);
//        else if (timeSinceDayStart[0] > studyPeriodEnd)
//            progressBar.setProgress((int) studyPeriodLength);
//        else
//            progressBar.setProgress((int) timeSinceStudyPeriodStart[0]);
//
//
//        countDownTimer = new CountDownTimer(timeToDayEnd, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                currentTime[0] = new Date().getTime();
//                System.out.println(App.getHoursAndMinutesTimeFromUnix(currentTime[0]));
//                timeSinceDayStart[0] = currentTime[0] - dayStart;
//                if (timeSinceDayStart[0] > studyPeriodStart && timeSinceDayStart[0] < studyPeriodEnd) {
//                    timeSinceStudyPeriodStart[0] = currentTime[0] - dayStart - studyPeriodStart;
//                    progressBar.setProgress((int) timeSinceStudyPeriodStart[0]);
//                    System.out.println(progressBar.getProgress());
//                    System.out.println("HAPPENED");
//                }
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//        countDownTimer.start();

        semesterPlaceholder = view.findViewById(R.id.semesterPlaceholder);

        semesterRecyclerView = view.findViewById(R.id.semesterRecyclerView);
        semesterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


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
