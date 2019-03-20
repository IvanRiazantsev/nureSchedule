package com.example.ivanriazantsev.nureschedule;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapters.WeekSection;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.Group;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
import events.Event;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class WeekFragment extends Fragment {

    View view;
    RecyclerView eventsRecyclerView;
    Timer mTimer;
    MyTimerTask mTimerTask;

    public static RecyclerView weekRecyclerView;
    public static SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    public static TextView weekPlaceholder;
    private AppDatabase database = App.getDatabase();
    private GroupDAO groupDAO = database.groupDAO();
    private EventDAO eventDAO = database.eventDAO();


    public WeekFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week, container, false);

        view.setOnClickListener(onOutsideClickListener);

        weekPlaceholder = view.findViewById(R.id.weekPlaceholder);


        weekRecyclerView = view.findViewById(R.id.weekRecyclerView);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        weekRecyclerView.setOnClickListener(onOutsideClickListener);
        weekRecyclerView.setItemAnimator(null);

        Group selectedGroup = groupDAO.getSelected();
        if (selectedGroup != null) {
            MyHandler handler = new MyHandler((MainActivity) getActivity());
            new Thread(() -> {
                Date currentDate = new Date();
                Date currentDay = App.getStartOfDay(currentDate);
                Date dayInAWeek = App.getStartOfDayInAWeek(currentDate);
                List<Event> eventsForWeek = eventDAO.getEventsBetweenTwoDatesForGroup(selectedGroup.getName(),
                        (int) (currentDay.getTime() / 1000L), (int) (dayInAWeek.getTime() / 1000L));

                for (int i = 0; i < eventsForWeek.size(); i++) {
                    List<Integer> list = eventsForWeek.get(i).getGroups();
                    if (!list.contains(groupDAO.getByName(selectedGroup.getName()).getId())) {
                        eventsForWeek.remove(i);
                        i--;
                    }
                }

                List<Event> firstDay = new ArrayList<>();
                List<Event> secondDay = new ArrayList<>();
                List<Event> thirdDay = new ArrayList<>();
                List<Event> fourthDay = new ArrayList<>();
                List<Event> fifthDay = new ArrayList<>();
                List<Event> sixthDay = new ArrayList<>();
                List<Event> seventhDay = new ArrayList<>();

                Date[] dates = App.getWeek(currentDay);
                ArrayList<List<Event>> eventsByDaysList = new ArrayList<>();

                for (Event event : eventsForWeek) {
                    if (event.getStartTime() < dates[1].getTime() / 1000)
                        firstDay.add(event);
                    else if (event.getStartTime() < dates[2].getTime() / 1000)
                        secondDay.add(event);
                    else if (event.getStartTime() < dates[3].getTime() / 1000)
                        thirdDay.add(event);
                    else if (event.getStartTime() < dates[4].getTime() / 1000)
                        fourthDay.add(event);
                    else if (event.getStartTime() < dates[5].getTime() / 1000)
                        fifthDay.add(event);
                    else if (event.getStartTime() < dates[6].getTime() / 1000)
                        sixthDay.add(event);
                    else
                        seventhDay.add(event);
                }


                removeDuplicates(firstDay);
                removeDuplicates(secondDay);
                removeDuplicates(thirdDay);
                removeDuplicates(fourthDay);
                removeDuplicates(fifthDay);
                removeDuplicates(sixthDay);
                removeDuplicates(seventhDay);


                eventsByDaysList.add(0, firstDay);
                eventsByDaysList.add(1, secondDay);
                eventsByDaysList.add(2, thirdDay);
                eventsByDaysList.add(3, fourthDay);
                eventsByDaysList.add(4, fifthDay);
                eventsByDaysList.add(5, sixthDay);
                eventsByDaysList.add(6, seventhDay);

                WeekFragment.sectionAdapter.removeAllSections();
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[0].getTime()), eventsByDaysList.get(0)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[1].getTime()), eventsByDaysList.get(1)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[2].getTime()), eventsByDaysList.get(2)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[3].getTime()), eventsByDaysList.get(3)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[4].getTime()), eventsByDaysList.get(4)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[5].getTime()), eventsByDaysList.get(5)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[6].getTime()), eventsByDaysList.get(6)));
                handler.sendEmptyMessage(1);
            }).start();


//            WeekFragment.weekRecyclerView.setAdapter(WeekFragment.sectionAdapter);
            MainActivity.selectedScheduleName.setText(selectedGroup.getName());
            MainActivity.selectedScheduleName.setVisibility(View.VISIBLE);
            MainActivity.bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);

            WeekFragment.weekPlaceholder.setVisibility(View.GONE);
        }


        return view;
    }

    private class MyHandler extends Handler {
        WeakReference<MainActivity> mainActivityWeakReference;

        MyHandler(MainActivity activity) {
            mainActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                if (msg.what == 1)
                    WeekFragment.weekRecyclerView.setAdapter(WeekFragment.sectionAdapter);

            }

        }
    }

    private void removeDuplicates(List<Event> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i + 1 != list.size()) {
                if (list.get(i).getSubjectId().equals(list.get(i + 1).getSubjectId())
                        && list.get(i).getNumberPair().equals(list.get(i + 1).getNumberPair())
                        && list.get(i).getType().equals(list.get(i + 1).getType())) {
                    list.get(i).setAuditory(list.get(i).getAuditory() + ", " + list.get(i + 1).getAuditory());
                    List<Integer> teachers = list.get(i).getTeachers();
                    if (list.get(i + 1).getTeachers() != null) {
                        teachers.addAll(list.get(i + 1).getTeachers());
                        list.get(i).setTeachers(teachers);
                    }
                    list.remove(i + 1);
                    i--;
                }
            }
        }
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
