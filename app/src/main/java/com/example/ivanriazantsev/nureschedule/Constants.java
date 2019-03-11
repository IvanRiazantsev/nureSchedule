package com.example.ivanriazantsev.nureschedule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.example.ivanriazantsev.nureschedule.Constants.ViewType.GROUP;
import static com.example.ivanriazantsev.nureschedule.Constants.ViewType.TEACHER;

public class Constants {

    @IntDef({GROUP, TEACHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewType {
        int GROUP = 100;
        int TEACHER = 200;
    }
}
