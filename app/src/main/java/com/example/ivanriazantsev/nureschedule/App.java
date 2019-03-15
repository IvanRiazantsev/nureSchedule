package com.example.ivanriazantsev.nureschedule;

import android.app.Application;

import java.util.Calendar;
import java.util.Date;

import androidx.room.Room;
import database.AppDatabase;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static CistAPI cistAPI;
    private Retrofit retrofit;
    private final String API_BASE_URL = "http://cist.nure.ua/ias/app/tt/";
    private static final String key = "IRtablet";
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cistAPI = retrofit.create(CistAPI.class);

        //TODO: no main thread queries
        database = Room.databaseBuilder(this,AppDatabase.class,"database").allowMainThreadQueries().build();
    }

    public static CistAPI getCistAPI() {
        return cistAPI;
    }

    public static String getKey() {
        return key;
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    public static Date getDateFromUnix(Long timestamp) {
        return new java.util.Date(timestamp * 1000L);
    }
    public static Calendar getCalendarFromUnix(Long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000L);
        return calendar;
    }
}
