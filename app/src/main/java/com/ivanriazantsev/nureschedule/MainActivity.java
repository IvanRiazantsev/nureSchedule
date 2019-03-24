package com.ivanriazantsev.nureschedule;

import adapters.AddGroupRecyclerViewAdapter;
import adapters.SavedGroupsRecyclerViewAdapter;
import adapters.WeekSection;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.Department;
import api.Direction;
import api.Faculty;
import api.Group;
import api.Main;
import api.Speciality;
import api.Teacher;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
import database.TeacherDAO;
import events.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {


    public static Toolbar toolbar;

    public static final Fragment weekFragment = new WeekFragment();
    final Fragment semesterFragment = new SemesterFragment();
    final Fragment settingsFragment = new SettingsFragment();

    final FragmentManager fm = getSupportFragmentManager();
    public static Fragment active;
    static BottomNavigationView bottomNavigationView;
    AppDatabase database = App.getDatabase();
    GroupDAO groupDAO = database.groupDAO();
    TeacherDAO teacherDAO = database.teacherDAO();
    EventDAO eventDAO = database.eventDAO();



    FloatingActionButton groupFAB;
    FloatingActionButton addGroupFAB;
    public static FloatingActionButton refreshGroupsFAB;
    public static FloatingActionButton deleteGroupsFAB;
    public static BottomSheetBehavior bottomSheetBehaviorSavedGroups;
    public static BottomSheetBehavior bottomSheetBehaviorAddGroups;


    private RecyclerView savedGroupsRecyclerView;
    public static SavedGroupsRecyclerViewAdapter savedGroupsAdapter;
    public List<Group> savedGroupsList = new ArrayList<>();
    public List<Teacher> savedTeachersList = new ArrayList<>();
    public static TextView savedGroupsPlaceholder;

    private RecyclerView groupsTeachersRecyclerView;
    public static AddGroupRecyclerViewAdapter addGroupRecyclerViewAdapter;
    public static TextInputEditText searchEditText;
    private List<Group> groupList = new ArrayList<>();
    private List<Teacher> teacherList = new ArrayList<>();
    public TextView addGroupsPlaceholder;

    boolean isSettingsOnScreen = false;

    private FloatingActionButton doneFAB;

    public static TextView selectedScheduleName;

    DatePicker datePicker;

    public static BillingClient billingClient;


    public static BottomSheetBehavior bottomSheetBehaviorInfo;
    public static TextView bottomEventName;
    public static TextView bottomType;
    public static TextView bottomRoom;
    public static TextView bottomTeacher;
    public static TextView bottomGroups;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem settings = menu.findItem(R.id.settingsToolbarItem);
        MenuItem calendar = menu.findItem(R.id.calendarToolbarItem);
        if (!isSettingsOnScreen && active == weekFragment) {
            settings.setVisible(true);
            calendar.setVisible(false);
        } else if (!isSettingsOnScreen && active == semesterFragment && groupDAO.getSelected() != null) {
            settings.setVisible(true);
            calendar.setVisible(true);
        } else if (!isSettingsOnScreen && active == semesterFragment) {
            settings.setVisible(true);
            calendar.setVisible(false);
        } else {
            settings.setVisible(false);
            calendar.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
        switch (item.getItemId()) {
            case R.id.calendarToolbarItem:
                datePicker = SemesterFragment.datePickerBuilder.build();
                datePicker.show();
                return true;
            case R.id.settingsToolbarItem:
                isSettingsOnScreen = true;
                selectedScheduleName.setVisibility(View.GONE);
                onPrepareOptionsMenu(toolbar.getMenu());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
                bottomNavigationView.postDelayed(() -> bottomNavigationView.setVisibility(View.GONE), 300);
                fm.beginTransaction().hide(active).show(settingsFragment).commit();
                bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
                ((TextView) findViewById(R.id.settingsToolbarTitle)).setVisibility(View.VISIBLE);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        if (bottomSheetBehaviorSavedGroups.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (bottomSheetBehaviorAddGroups.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if (isSettingsOnScreen) {
            isSettingsOnScreen = false;
//            selectedScheduleName.setVisibility(View.VISIBLE);
            onPrepareOptionsMenu(toolbar.getMenu());
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            fm.beginTransaction().hide(semesterFragment).show(active).commit();
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.animate().translationY(0);
            ((TextView) findViewById(R.id.settingsToolbarTitle)).setVisibility(View.GONE);
        } else if (bottomSheetBehaviorSavedGroups.getState() == BottomSheetBehavior.STATE_COLLAPSED
                && bottomSheetBehaviorAddGroups.getState() == BottomSheetBehavior.STATE_COLLAPSED && bottomSheetBehaviorInfo.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            finish();
        } else if (bottomSheetBehaviorInfo.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        selectedScheduleName.setVisibility(View.VISIBLE);

    }

    @SuppressLint({"WrongThread", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        active = weekFragment;

        billingClient = BillingClient.newBuilder(this).setListener(this).build();


//        database.clearAllTables();

        bottomSheetBehaviorSavedGroups = BottomSheetBehavior.from(findViewById(R.id.groupsBottomSheet));
        bottomSheetBehaviorAddGroups = BottomSheetBehavior.from(findViewById(R.id.addGroupBottomSheet));
        bottomSheetBehaviorInfo = BottomSheetBehavior.from(findViewById(R.id.infoBottomSheet));

        bottomEventName = findViewById(R.id.popupEventName);
        bottomGroups = findViewById(R.id.popupGroups);
        bottomRoom = findViewById(R.id.popupRoom);
        bottomType = findViewById(R.id.popupType);
        bottomTeacher = findViewById(R.id.popupTeacher);

        addGroupFAB = findViewById(R.id.addGroupButton1);
        addGroupFAB.setOnClickListener(addGroupButtonListener);

        refreshGroupsFAB = findViewById(R.id.refreshGroupsButton);

        doneFAB = findViewById(R.id.doneFAB);
        doneFAB.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                InputMethodManager inputManager =
                        (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }, 80);
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        addOnBackPressedCallback(() -> true);


        bottomNavigationView = findViewById(R.id.bottomNavView);
        initializeFragments();

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


        groupFAB = findViewById(R.id.fab_button);
        groupFAB.setOnClickListener(v -> {
            bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        bottomSheetBehaviorSavedGroups.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    refreshGroupsFAB.setVisibility(View.GONE);
                    deleteGroupsFAB.setVisibility(View.GONE);
                    addGroupFAB.setVisibility(View.GONE);
                }
                if (i == BottomSheetBehavior.STATE_EXPANDED && savedGroupsList.size() != 0) {
                    refreshGroupsFAB.setVisibility(View.VISIBLE);
                    deleteGroupsFAB.setVisibility(View.VISIBLE);
                }
            }


            @SuppressLint("RestrictedApi")
            @Override
            public void onSlide(@NonNull View view, float v) {
                if (savedGroupsPlaceholder.getVisibility() == View.GONE) {
                    refreshGroupsFAB.setVisibility(View.VISIBLE);
                    deleteGroupsFAB.setVisibility(View.VISIBLE);
                }
                addGroupFAB.setVisibility(View.VISIBLE);

                refreshGroupsFAB.animate().scaleX(v).scaleY(v).setDuration(0).start();
                deleteGroupsFAB.animate().scaleX(v).scaleY(v).setDuration(0).start();
                addGroupFAB.animate().scaleX(v).scaleY(v).setDuration(0).start();
            }
        });

        savedGroupsPlaceholder = findViewById(R.id.savedGroupsPlaceholder);
        savedGroupsRecyclerView = findViewById(R.id.savedGroupsRecyclerView);
        savedGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedGroupsAdapter = new SavedGroupsRecyclerViewAdapter();
        savedGroupsRecyclerView.setAdapter(savedGroupsAdapter);


        savedGroupsList.addAll(groupDAO.getAllAdded());
        savedTeachersList.addAll(teacherDAO.getAllAdded());


        if (!savedGroupsList.isEmpty() || !savedTeachersList.isEmpty()) {
            savedGroupsPlaceholder.setVisibility(View.GONE);
        }
        if ((!savedGroupsList.isEmpty() || !savedTeachersList.isEmpty())
                && bottomSheetBehaviorSavedGroups.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            refreshGroupsFAB.setVisibility(View.VISIBLE);
            deleteGroupsFAB.setVisibility(View.VISIBLE);
        }


        savedGroupsAdapter.setList(savedGroupsList, savedTeachersList);


        searchEditText = findViewById(R.id.groupSearchEditText);


        addGroupsPlaceholder = findViewById(R.id.addGroupPlaceholder);

        groupsTeachersRecyclerView = findViewById(R.id.groupsTeachersRecyclerView);
        groupsTeachersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addGroupRecyclerViewAdapter = new AddGroupRecyclerViewAdapter();
        groupsTeachersRecyclerView.setAdapter(addGroupRecyclerViewAdapter);

        searchEditText.addTextChangedListener(textWatcher);

        App.getCistAPI().getGroupsList().enqueue(new Callback<Main>() {
            @Override
            public void onResponse(@NonNull Call<Main> call, @NonNull Response<Main> response) {
                try {
                    assert response.body() != null;
                    List<Faculty> list = response.body().getUniversity().getFaculties();
                    for (Faculty faculty : list) {
                        List<Direction> list1 = faculty.getDirections();
                        for (Direction direction : list1) {
                            if (direction.getGroups() != null) {
                                groupList.addAll(direction.getGroups());
                            }
                            if (direction.getSpecialities() != null) {
                                for (Speciality speciality : direction.getSpecialities()) {
                                    groupList.addAll(speciality.getGroups());
                                }
                            }
                        }
                    }
                    Collections.sort(groupList, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    addGroupsPlaceholder.setText("Сервер недоступен");
                    searchEditText.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Main> call, @NonNull Throwable t) {
                t.printStackTrace();
                addGroupsPlaceholder.setText("Сервер недоступен");
                searchEditText.setVisibility(View.GONE);
            }
        });

        App.getCistAPI().getTeachersList().enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {
                if (response.code() != 200)
                    return;
                List<Faculty> list = response.body().getUniversity().getFaculties();
                for (Faculty faculty : list) {
                    List<Department> list1 = faculty.getDepartments();
                    for (Department department : list1) {
                        teacherList.addAll(department.getTeachers());
                    }
                }
                Collections.sort(teacherList, (o1, o2) -> o1.getShortName().compareToIgnoreCase(o2.getShortName()));
            }

            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                t.printStackTrace();
                addGroupsPlaceholder.setText("Сервер недоступен");
                searchEditText.setVisibility(View.GONE);
            }
        });

        addGroupRecyclerViewAdapter.setList(groupList, teacherList);

        refreshGroupsFAB.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show());

        deleteGroupsFAB = findViewById(R.id.deleteGroupsButton);
        deleteGroupsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deletionDialog = new AlertDialog.Builder(MainActivity.this);
                deletionDialog.setCancelable(true);
                deletionDialog.setTitle("Удалить все расписания");
                deletionDialog.setMessage("Вы уверены, что хотите удалить все сохраненные расписания?");
                deletionDialog.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.clearAllTables();
                        for (Group group : groupList) {
                            group.setRefreshDate("Не обновлялось");
                        }
                        for (Teacher teacher : teacherList) {
                            teacher.setRefreshDate("Не обновлялось");
                        }
                        addGroupRecyclerViewAdapter.clearList();
                        addGroupRecyclerViewAdapter.setList(groupList, teacherList);
                        selectedScheduleName.setText("");
                        WeekFragment.sectionAdapter.removeAllSections();
                        WeekFragment.sectionAdapter.notifyDataSetChanged();
                        savedGroupsAdapter.clearList();
                        savedGroupsPlaceholder.setVisibility(View.VISIBLE);
                        WeekFragment.weekPlaceholder.setVisibility(View.VISIBLE);
                        refreshGroupsFAB.setVisibility(View.GONE);
                        deleteGroupsFAB.setVisibility(View.GONE);


                        SemesterFragment.semesterPlaceholder.setVisibility(View.VISIBLE);
                        SemesterFragment.timeline.setVisibility(View.GONE);
                        SemesterFragment.semesterRecyclerView.setVisibility(View.GONE);
                        SemesterFragment.backToTodayFAB.setVisibility(View.GONE);
                        SemesterFragment.semesterRecyclerAdapter.clearList();
                        SemesterFragment.semesterRecyclerAdapter.notifyDataSetChanged();
                    }
                });
                deletionDialog.setNegativeButton("Отменить", null);
                deletionDialog.show();




            }
        });

        selectedScheduleName = findViewById(R.id.selectedScheduleName);
        if (groupDAO.getSelected() != null || teacherDAO.getSelected() != null) {
            Object selected = groupDAO.getSelected() != null ? groupDAO.getSelected() : teacherDAO.getSelected();

            Date currentDate = new Date();
            Date currentDay = App.getStartOfDay(currentDate);
            Date dayInAWeek = App.getStartOfDayInAWeek(currentDate);
            List<Event> eventsForWeek = eventDAO.getEventsBetweenTwoDatesForGroup(selected instanceof Group ? ((Group) selected).getName() : ((Teacher) selected).getShortName(),
                    (int) (currentDay.getTime() / 1000L), (int) (dayInAWeek.getTime() / 1000L));


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


//            WeekFragment.weekRecyclerView.setAdapter(WeekFragment.sectionAdapter);
            MainActivity.selectedScheduleName.setText(selected instanceof Group ? ((Group) selected).getName() : ((Teacher) selected).getShortName());
            MainActivity.selectedScheduleName.setVisibility(View.VISIBLE);
            MainActivity.bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);

//            WeekFragment.weekPlaceholder.setVisibility(View.GONE);

            if (selected instanceof Group) {
                if (groupDAO.getSelected() != null)
                    groupDAO.updateIsSelected(false, groupDAO.getSelected().getName());
                groupDAO.updateIsSelected(true, ((Group) selected).getName());
            } else {
                if (teacherDAO.getSelected() != null)
                    teacherDAO.updateIsSelected(false, teacherDAO.getSelected().getShortName());
                teacherDAO.updateIsSelected(true, ((Teacher) selected).getShortName());
            }


            List<List<Event>> events = new ArrayList<>();

            Date begin = App.getStartOfDay(App.getDateFromUnix((long) (eventDAO.getMinTimeForGroup(
                    selected instanceof Group ? groupDAO.getSelected().getName() : teacherDAO.getSelected().getShortName()))));

            Date end = App.getStartOfNextDay(App.getStartOfDay(App.getDateFromUnix((long) (eventDAO.getMaxTimeForGroup(
                    selected instanceof Group ? groupDAO.getSelected().getName() : teacherDAO.getSelected().getShortName())))));

            long millisSinceBegin = new Date().getTime() - begin.getTime();
            long daysSinceBegin = millisSinceBegin / 1000 / 60 / 60 / 24;

            ArrayMap<Event, List<Event>> simultaneousEvents = new ArrayMap<>();


            for (long i = begin.getTime(); i < end.getTime(); i += 86400000) {
                List<Event> eventsForDay = eventDAO.getEventsBetweenTwoDatesForGroup(
                        selected instanceof Group ? groupDAO.getSelected().getName() : teacherDAO.getSelected().getShortName(),
                        (int) (i / 1000), (int) ((i + 86400000) / 1000));

                for (int j = 0; j < eventsForDay.size(); j++) {
                    Integer startTime = eventsForDay.get(j).getStartTime();
                    Event current = eventsForDay.get(j);
                    List<Event> events1 = new ArrayList<>();
                    for (int q = j + 1; q < eventsForDay.size(); q++) {
                        if (startTime.equals(eventsForDay.get(q).getStartTime())) {
                            events1.add(eventsForDay.get(q));
                            eventsForDay.remove(q);
                            q--;
                        }
                    }
                    simultaneousEvents.put(current, events1);
                }
                events.add(eventsForDay);
            }

            SemesterFragment.semesterRecyclerAdapter.clearList();
            SemesterFragment.semesterRecyclerAdapter.setSimultaneousEvents(events, simultaneousEvents);
//            SemesterFragment.semesterRecyclerView.setItemAnimator(null);
//            SemesterFragment.semesterRecyclerView.setAdapter(SemesterFragment.semesterRecyclerAdapter);
//            SemesterFragment.semesterPlaceholder.setVisibility(View.GONE);
//            SemesterFragment.timeline.setVisibility(View.VISIBLE);
//            SemesterFragment.semesterRecyclerView.setVisibility(View.VISIBLE);
//            SemesterFragment.semesterRecyclerView.scrollToPosition((int) daysSinceBegin);
//            SemesterFragment.backToTodayFAB.setVisibility(View.VISIBLE);

            if (groupDAO.getSelected() != null) {

                SemesterFragment.datePickerBuilder = new DatePickerBuilder(this, new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        SemesterFragment.semesterRecyclerView.scrollToPosition((int) ((calendar.get(0).getTimeInMillis() - begin.getTime()) / 1000 / 60 / 60 / 24));
                    }
                }).pickerType(CalendarView.ONE_DAY_PICKER).minimumDate(App.getStartOfDayCalendar(App.getDateFromUnix((long) eventDAO.getMinTimeForGroup(groupDAO.getSelected().getName()))))
                        .maximumDate(App.getStartOfDayCalendar(App.getDateFromUnix((long) eventDAO.getMaxTimeForGroup(groupDAO.getSelected().getName()))))
                        .daysLabelsColor(R.color.colorAccent).pagesColor(R.color.pagesColor).headerColor(R.color.colorBackground);
            } else if (teacherDAO.getSelected() != null) {
                SemesterFragment.datePickerBuilder = new DatePickerBuilder(this, new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        SemesterFragment.semesterRecyclerView.scrollToPosition((int) ((calendar.get(0).getTimeInMillis() - begin.getTime()) / 1000 / 60 / 60 / 24));
                    }
                }).pickerType(CalendarView.ONE_DAY_PICKER).minimumDate(App.getStartOfDayCalendar(App.getDateFromUnix((long) eventDAO.getMinTimeForGroup(teacherDAO.getSelected().getShortName()))))
                        .maximumDate(App.getStartOfDayCalendar(App.getDateFromUnix((long) eventDAO.getMaxTimeForGroup(teacherDAO.getSelected().getShortName()))))
                        .daysLabelsColor(R.color.colorAccent).pagesColor(R.color.pagesColor).headerColor(R.color.colorBackground);
            }

            if (MainActivity.active != MainActivity.weekFragment)
                MainActivity.toolbar.getMenu().findItem(R.id.calendarToolbarItem).setVisible(true);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.requestFocus();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        toolbar.requestFocus();
    }

    private void filter(String text) {
        List<Object> filteredList = new ArrayList<>();
        String textReplaced = text.replace("и", "і");

        for (Group group : groupList)
            if (group.getName().toLowerCase().contains(text) || group.getName().toLowerCase().contains(textReplaced))
                filteredList.add(group);
        for (Teacher teacher : teacherList)
            if (teacher.getShortName().toLowerCase().contains(text) || teacher.getShortName().toLowerCase().contains(textReplaced))
                filteredList.add(teacher);

        if (filteredList.size() != 0) {
            addGroupsPlaceholder.setVisibility(View.GONE);
            groupsTeachersRecyclerView.setVisibility(View.VISIBLE);
            addGroupRecyclerViewAdapter.filterList(filteredList);
        } else {
            addGroupsPlaceholder.setText("Не найдено");
            addGroupsPlaceholder.setVisibility(View.VISIBLE);
            groupsTeachersRecyclerView.setVisibility(View.GONE);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String searchString = s.toString().trim().toLowerCase();
                if (!searchString.isEmpty()) {
                    addGroupsPlaceholder.setVisibility(View.GONE);
                    groupsTeachersRecyclerView.setVisibility(View.VISIBLE);
                    filter(searchString);
                } else {
                    addGroupsPlaceholder.setText("Начните писать");
                    addGroupsPlaceholder.setVisibility(View.VISIBLE);
                    groupsTeachersRecyclerView.setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
                Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_LONG).show();
            }
        }

    };


    private void initializeFragments() {
        fm.beginTransaction().add(R.id.main_container, settingsFragment, "settings").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, semesterFragment, "semester").hide(semesterFragment).commit();
        fm.beginTransaction().add(R.id.main_container, weekFragment, "week").commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.weekBottomNavigationItem:
                    fm.beginTransaction().hide(active).show(weekFragment).commit();
                    active = weekFragment;
                    toolbar.getMenu().findItem(R.id.calendarToolbarItem).setVisible(false);
                    return true;
                case R.id.semesterBottomNavigationItem:
                    fm.beginTransaction().hide(active).show(semesterFragment).commit();
                    active = semesterFragment;
                    if (groupDAO.getSelected() != null || teacherDAO.getSelected() != null) {
                        toolbar.getMenu().findItem(R.id.calendarToolbarItem).setVisible(true);
                        SemesterFragment.backToTodayFAB.setVisibility(View.VISIBLE);
                    } else {
                        toolbar.getMenu().findItem(R.id.calendarToolbarItem).setVisible(false);
                        SemesterFragment.backToTodayFAB.setVisibility(View.GONE);
                    }
                    return true;
            }
            return false;
        }
    };

    @SuppressLint("RestrictedApi")
    private View.OnClickListener addGroupButtonListener = v -> {
        bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_EXPANDED);
        deleteGroupsFAB.setVisibility(View.GONE);
        refreshGroupsFAB.setVisibility(View.GONE);


    };


    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

    }
}
