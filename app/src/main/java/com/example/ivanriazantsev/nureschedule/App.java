package com.example.ivanriazantsev.nureschedule;

import android.app.Application;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static CistAPI cistAPI;
    private Retrofit retrofit;
    private final String API_BASE_URL = "https://jsonplaceholder.typicode.com/";


    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cistAPI = retrofit.create(CistAPI.class);
    }

    public static CistAPI getCistAPI() {
        return cistAPI;
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
