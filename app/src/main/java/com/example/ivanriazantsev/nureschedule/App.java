package com.example.ivanriazantsev.nureschedule;

import android.app.Application;
import android.graphics.Color;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


import androidx.collection.ArrayMap;
import androidx.room.Room;
import database.AppDatabase;
import okhttp3.OkHttpClient;
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

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                .build();

        cistAPI = retrofit.create(CistAPI.class);

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

    public static String getCurrentFullDate() {
        SimpleDateFormat simpleDateFormatArrivals = new SimpleDateFormat("dd MMMM yyyy HH:mm", locale);
        return simpleDateFormatArrivals.format(new Date());
    }

    public static String getDateForWeek(Long unixTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMMM yyyy", locale);
        return simpleDateFormat.format(getDateFromUnix(unixTime / 1000L));
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getStartOfDayInAWeek(Date date) {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 7);
        return getStartOfDay(calendar.getTime());
    }

    public static Date[] getWeek(Date date) {
        Date[] dates = new Date[7];
        dates[0] = date;
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        for (int i = 1; i < dates.length; i++) {
            calendar.add(Calendar.DATE, 1);
            dates[i] = calendar.getTime();
        }
        return dates;
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
