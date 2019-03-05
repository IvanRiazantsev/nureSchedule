package com.example.ivanriazantsev.nureschedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    final Fragment weekFragment = new WeekFragment();
    final Fragment semesterFragment = new SemesterFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = weekFragment;

    BottomNavigationView bottomNavigationView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendarToolbarItem:
                Toast.makeText(this, "Calendar", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        System.out.println(App.getCalendarFromUnix(1550773437L));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottomNavigationView = findViewById(R.id.bottomNavView);


        fm.beginTransaction().add(R.id.main_container, settingsFragment, "settings").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, semesterFragment, "semester").hide(semesterFragment).commit();
        fm.beginTransaction().add(R.id.main_container, weekFragment, "week").commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


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
