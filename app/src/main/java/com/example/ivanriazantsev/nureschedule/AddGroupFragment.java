package com.example.ivanriazantsev.nureschedule;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import adapters.AddGroupRecyclerViewAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.Direction;
import api.Faculty;
import api.Group;
import api.Main;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddGroupFragment extends Fragment {

    private View view;
    private RecyclerView groupsTeachersRecyclerView;
    private AddGroupRecyclerViewAdapter addGroupRecyclerViewAdapter;

    public AddGroupFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_group, container, false);

        List<Group> groupList = new ArrayList<>();

        groupsTeachersRecyclerView = view.findViewById(R.id.groupsTeachersRecyclerView);
        groupsTeachersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addGroupRecyclerViewAdapter = new AddGroupRecyclerViewAdapter();
        groupsTeachersRecyclerView.setAdapter(addGroupRecyclerViewAdapter);



        App.getCistAPI().getGroupsList().enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {
                assert response.body() != null;
                List<Faculty> list = response.body().getUniversity().getFaculties();
                for (Faculty faculty : list) {
                    List<Direction> list1 = faculty.getDirections();
                    for (Direction direction : list1)
                        if(direction.getGroups() != null) {
                            addGroupRecyclerViewAdapter.setList(direction.getGroups());
                        }
                }
            }

            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                System.out.println("Something went wrong!");
            }
        });



        return view;
    }

}
