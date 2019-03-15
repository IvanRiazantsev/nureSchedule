package com.example.ivanriazantsev.nureschedule;


import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import adapters.AddGroupRecyclerViewAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.Department;
import api.Direction;
import api.Faculty;
import api.Group;
import api.Main;
import api.Speciality;
import api.Teacher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddGroupFragment extends Fragment {

    private View view;
    private RecyclerView groupsTeachersRecyclerView;
    private AddGroupRecyclerViewAdapter addGroupRecyclerViewAdapter;
    private TextInputEditText searchEditText;
    private List<Group> groupList = new ArrayList<>();
    private List<Teacher> teacherList = new ArrayList<>();
    private TextView placeholder;

    public AddGroupFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_group, container, false);


        searchEditText = view.findViewById(R.id.groupSearchEditText);
        placeholder = view.findViewById(R.id.addGroupPlaceholder);

        groupsTeachersRecyclerView = view.findViewById(R.id.groupsTeachersRecyclerView);
        groupsTeachersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addGroupRecyclerViewAdapter = new AddGroupRecyclerViewAdapter();
        groupsTeachersRecyclerView.setAdapter(addGroupRecyclerViewAdapter);


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
                    placeholder.setText("Сервер недоступен");
                    searchEditText.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Main> call, @NonNull Throwable t) {
                t.printStackTrace();
                placeholder.setText("Сервер недоступен");
                searchEditText.setVisibility(View.GONE);
            }
        });

        App.getCistAPI().getTeachersList().enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {
                if (response.code() != 200)
                    return;
                List<Faculty> list = response.body().getUniversity().getFaculties();
                for (Faculty faculty : list){
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
                placeholder.setText("Сервер недоступен");
                searchEditText.setVisibility(View.GONE);
            }
        });

        addGroupRecyclerViewAdapter.setList(groupList, teacherList);

        searchEditText.addTextChangedListener(textWatcher);


        return view;
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
            placeholder.setVisibility(View.GONE);
            groupsTeachersRecyclerView.setVisibility(View.VISIBLE);
            addGroupRecyclerViewAdapter.filterList(filteredList);
        } else {
            placeholder.setText("Не найдено");
            placeholder.setVisibility(View.VISIBLE);
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
                    placeholder.setVisibility(View.GONE);
                    groupsTeachersRecyclerView.setVisibility(View.VISIBLE);
                    filter(searchString);
                } else {
                    placeholder.setText("Начните писать");
                    placeholder.setVisibility(View.VISIBLE);
                    groupsTeachersRecyclerView.setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), "Server error", Toast.LENGTH_LONG).show();
            }
        }

    };


}
