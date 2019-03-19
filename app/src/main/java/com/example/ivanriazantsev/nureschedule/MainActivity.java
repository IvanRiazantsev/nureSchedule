package com.example.ivanriazantsev.nureschedule;

import adapters.AddGroupRecyclerViewAdapter;
import adapters.SavedGroupsRecyclerViewAdapter;
import androidx.annotation.NonNull;
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
import database.GroupDAO;
import database.TeacherDAO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;

    final Fragment weekFragment = new WeekFragment();
    final Fragment semesterFragment = new SemesterFragment();
    final Fragment settingsFragment = new SettingsFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = weekFragment;
    static BottomNavigationView bottomNavigationView;
    AppDatabase database = App.getDatabase();
    GroupDAO groupDAO = database.groupDAO();
    TeacherDAO teacherDAO = database.teacherDAO();
    FloatingActionButton groupFAB;
    FloatingActionButton addGroupFAB;
    public static FloatingActionButton refreshGroupsFAB;
    public static BottomSheetBehavior bottomSheetBehaviorSavedGroups;
    static BottomSheetBehavior bottomSheetBehaviorAddGroups;


    private RecyclerView savedGroupsRecyclerView;
    public static SavedGroupsRecyclerViewAdapter savedGroupsAdapter;
    public List<Group> savedGroupsList = new ArrayList<>();
    public List<Teacher> savedTeachersList = new ArrayList<>();
    public static TextView savedGroupsPlaceholder;

    private RecyclerView groupsTeachersRecyclerView;
    public AddGroupRecyclerViewAdapter addGroupRecyclerViewAdapter;
    public static TextInputEditText searchEditText;
    private List<Group> groupList = new ArrayList<>();
    private List<Teacher> teacherList = new ArrayList<>();
    public TextView addGroupsPlaceholder;

    boolean isSettingsOnScreen = false;

    private FloatingActionButton doneFAB;

    public static TextView selectedScheduleName;




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
        } else if (!isSettingsOnScreen && active == semesterFragment) {
            settings.setVisible(true);
            calendar.setVisible(true);
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
                Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
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
                && bottomSheetBehaviorAddGroups.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            finish();
        }

    }

    @SuppressLint({"WrongThread", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        database.clearAllTables();

        bottomSheetBehaviorSavedGroups = BottomSheetBehavior.from(findViewById(R.id.groupsBottomSheet));
        bottomSheetBehaviorAddGroups = BottomSheetBehavior.from(findViewById(R.id.addGroupBottomSheet));

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
            }, 50);
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
                    addGroupFAB.setVisibility(View.GONE);
                }
            }


            @SuppressLint("RestrictedApi")
            @Override
            public void onSlide(@NonNull View view, float v) {
                if (savedGroupsPlaceholder.getVisibility() == View.GONE)
                    refreshGroupsFAB.setVisibility(View.VISIBLE);
                addGroupFAB.setVisibility(View.VISIBLE);

                refreshGroupsFAB.animate().scaleX(v).scaleY(v).setDuration(0).start();
                addGroupFAB.animate().scaleX(v).scaleY(v).setDuration(0).start();
            }
        });

        savedGroupsPlaceholder = findViewById(R.id.savedGroupsPlaceholder);
        savedGroupsRecyclerView = findViewById(R.id.savedGroupsRecyclerView);
        savedGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedGroupsAdapter = new SavedGroupsRecyclerViewAdapter();
        savedGroupsRecyclerView.setAdapter(savedGroupsAdapter);


        savedGroupsList.addAll(groupDAO.getAll());
        savedTeachersList.addAll(teacherDAO.getAll());


        if (!savedGroupsList.isEmpty() || !savedTeachersList.isEmpty()) {
            savedGroupsPlaceholder.setVisibility(View.GONE);
        }
        if ((!savedGroupsList.isEmpty() || !savedTeachersList.isEmpty())
                && bottomSheetBehaviorSavedGroups.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            refreshGroupsFAB.setVisibility(View.VISIBLE);
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

        refreshGroupsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        selectedScheduleName = findViewById(R.id.selectedScheduleName);
        if (groupDAO.getSelected() != null) {
            selectedScheduleName.setText(groupDAO.getSelected().getName());
            selectedScheduleName.setVisibility(View.VISIBLE);
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
                    toolbar.getMenu().findItem(R.id.calendarToolbarItem).setVisible(true);
                    return true;
            }
            return false;
        }
    };

    private View.OnClickListener addGroupButtonListener = v -> {
        bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_EXPANDED);

    };


}
