package com.example.ivanriazantsev.nureschedule;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class SemesterFragment extends Fragment {

    View view;

    public SemesterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_semester, container, false);

        
        return view;
    }

}
