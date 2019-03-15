package com.example.ivanriazantsev.nureschedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import database.AppDatabase;
import database.GroupDAO;
import database.TeacherDAO;
import fr.ganfra.materialspinner.MaterialSpinner;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    final static Fragment weekFragment = new WeekFragment();
    final Fragment semesterFragment = new SemesterFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment groupsFragment = new GroupsFragment();
    final static Fragment addGroupFragment = new AddGroupFragment();

    final FragmentManager fm = getSupportFragmentManager();

    Fragment active = weekFragment;

    static boolean isGroupsFragmentsOnScreen = false;
    static boolean isAddGroupFragmentOnScreen = false;

    BottomNavigationView bottomNavigationView;

    MaterialSpinner spinner;
    AppDatabase database = App.getDatabase();
    GroupDAO groupDAO = database.groupDAO();
    TeacherDAO teacherDAO = database.teacherDAO();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendarToolbarItem:
                Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.groupsToolbarItem:
                fm.beginTransaction().setCustomAnimations(R.animator.slide_left, R.animator.slide_right).hide(active).show(groupsFragment).addToBackStack(null).commit();
                switchToolbarContent();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        addOnBackPressedCallback(() -> {
            switchToolbarContent();
            return true;
        });


        bottomNavigationView = findViewById(R.id.bottomNavView);
        initializeFragments();


        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


//        spinner = findViewById(R.id.groupsSpinner);
//
//        String[] savedGroupsList = new String[groupDAO.getAll().size()];
//        String[] savedTeachersList = new String[teacherDAO.getAll().size()];
//        for (int i = 0; i < savedGroupsList.length; i++) {
//            savedGroupsList[i] = groupDAO.getAll().get(i).getName();
//        }
//        for (int i = 0; i < savedTeachersList.length; i++) {
//            savedTeachersList[i] = teacherDAO.getAll().get(i).getShortName();
//        };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, savedList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);




    }


    private void initializeFragments() {
        fm.beginTransaction().add(R.id.main_container, settingsFragment, "settings").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, semesterFragment, "semester").hide(semesterFragment).commit();
        fm.beginTransaction().add(R.id.main_container, groupsFragment, "groups").hide(groupsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, weekFragment, "week").commit();
    }

    private void switchToolbarContent() {
        if (isGroupsFragmentsOnScreen) {
            fm.beginTransaction().setCustomAnimations(R.animator.slide_left, R.animator.slide_right).hide(groupsFragment).show(active).commit();
            toolbar.getMenu().setGroupVisible(R.id.mainToolbarGroup, true);
            findViewById(R.id.groupsSpinner).setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.animate().translationY(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            findViewById(R.id.groupsToolbarTitle).setVisibility(View.GONE);
            isGroupsFragmentsOnScreen = false;
        } else if (isAddGroupFragmentOnScreen) {
            fm.beginTransaction().setCustomAnimations(R.animator.slide_left, R.animator.slide_right).hide(addGroupFragment).show(groupsFragment).commit();
            findViewById(R.id.addGroupToolbarTitle).setVisibility(View.GONE);
            findViewById(R.id.groupsToolbarTitle).setVisibility(View.VISIBLE);
            isAddGroupFragmentOnScreen = false;
            isGroupsFragmentsOnScreen = true;
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
            findViewById(R.id.groupsSpinner).setVisibility(View.GONE);
            toolbar.getMenu().setGroupVisible(R.id.mainToolbarGroup, false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            findViewById(R.id.groupsToolbarTitle).setVisibility(View.VISIBLE);
            isGroupsFragmentsOnScreen = true;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.weekBottomNavigationItem:
                    fm.beginTransaction().hide(active).show(weekFragment).commit();
                    active = weekFragment;
                    toolbar.setVisibility(View.VISIBLE);
                    return true;
                case R.id.semesterBottomNavigationItem:
                    fm.beginTransaction().hide(active).show(semesterFragment).commit();
                    active = semesterFragment;
                    toolbar.setVisibility(View.VISIBLE);
                    return true;
                case R.id.settingsBottomNavigationItem:
                    fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    active = settingsFragment;
                    toolbar.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };


}
