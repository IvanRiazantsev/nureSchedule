package com.example.ivanriazantsev.nureschedule;

import android.app.Application;
import android.graphics.Color;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Locale;


import androidx.collection.ArrayMap;
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
    private final static Locale locale = new Locale("ru", "UA");
    public static ArrayMap<String, Integer> eventsColors = new ArrayMap<>();


    @Override
    public void onCreate() {
        super.onCreate();


        initializeEventsColors();

        retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cistAPI = retrofit.create(CistAPI.class);

        //TODO: no main thread queries
        database = Room.databaseBuilder(this, AppDatabase.class, "database").allowMainThreadQueries().build();
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

    public static String getHoursAndMinutesTimeFromUnix(Long unixTime) {
        SimpleDateFormat simpleDateFormatArrivals = new SimpleDateFormat("HH:mm", locale);
        return simpleDateFormatArrivals.format(getDateFromUnix(unixTime));
    }

    private void initializeEventsColors() {
//        //Lectures
//        eventsColors.put(0, Color.argb(255,235,201,87));
//        eventsColors.put(1, Color.argb(255,235,201,87));
//        eventsColors.put(2, Color.argb(255,235,201,87));
//
//        //practical
//        eventsColors.put(10, Color.argb(255,182,221,119));
//        eventsColors.put(12, Color.argb(255,182,221,119));
//
//        //laboratory
//        eventsColors.put(20, Color.argb(255,153,110,187));
//        eventsColors.put(21, Color.argb(255,153,110,187));
//        eventsColors.put(22, Color.argb(255,153,110,187));
//        eventsColors.put(23, Color.argb(255,153,110,187));
//        eventsColors.put(24, Color.argb(255,153,110,187));
//
//        //consultation
//        eventsColors.put(30, Color.argb(255,138,190,214));
//
//        //tests
//        eventsColors.put(40, Color.argb(255,113,214,198));
//        eventsColors.put(41, Color.argb(255,113,214,198));
//
//        //exams
//        eventsColors.put(50, Color.argb(255,179,29,29));
//        eventsColors.put(51, Color.argb(255,179,29,29));
//        eventsColors.put(52, Color.argb(255,179,29,29));
//        eventsColors.put(53, Color.argb(255,179,29,29));
//        eventsColors.put(54, Color.argb(255,179,29,29));

        eventsColors.put("lecture", Color.argb(255, 235, 201, 87));
        eventsColors.put("practice", Color.argb(255, 182, 221, 119));
        eventsColors.put("laboratory", Color.argb(255, 153, 110, 187));
        eventsColors.put("consultation", Color.argb(255, 138, 190, 214));
        eventsColors.put("test", Color.argb(255, 113, 214, 198));
        eventsColors.put("exam", Color.argb(255, 179, 29, 29));
        eventsColors.put("course_work", Color.argb(255, 184, 76, 146));


    }
}
