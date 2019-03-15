package com.example.ivanriazantsev.nureschedule;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapters.SavedGroupsRecyclerViewAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.Group;
import api.Teacher;
import database.AppDatabase;
import database.GroupDAO;
import database.TeacherDAO;


public class GroupsFragment extends Fragment {

    View view;
    private FloatingActionButton addGroupButton;
    private RecyclerView savedGroupsRecyclerView;
    public static SavedGroupsRecyclerViewAdapter adapter;
    public static List<Group> savedGroupsList = new ArrayList<>();
    public static List<Teacher> savedTeachersList = new ArrayList<>();
    private AppDatabase database = App.getDatabase();
    private GroupDAO groupDAO = database.groupDAO();
    private TeacherDAO teacherDAO = database.teacherDAO();
    public TextView placeholder;

    public GroupsFragment() {
    }


    //
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (isEmpty)
//            placeholder.setVisibility(View.VISIBLE);
//        else
//            placeholder.setVisibility(View.GONE);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups, container, false);

        placeholder = view.findViewById(R.id.savedGroupsPlaceholder);

        addGroupButton = view.findViewById(R.id.addGroupButton);

        addGroupButton.setOnClickListener(addGroupButtonListener);

        savedGroupsRecyclerView = view.findViewById(R.id.savedGroupsRecyclerView);
        savedGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavedGroupsRecyclerViewAdapter();
        savedGroupsRecyclerView.setAdapter(adapter);

        savedGroupsList.addAll(groupDAO.getAll());
        savedTeachersList.addAll(teacherDAO.getAll());

        if (!savedGroupsList.isEmpty() || !savedTeachersList.isEmpty()) {
            placeholder.setVisibility(View.GONE);
        }

        adapter.setList(savedGroupsList, savedTeachersList);





        return view;
    }

    private View.OnClickListener addGroupButtonListener = v -> {
        if (!MainActivity.addGroupFragment.isAdded())
            getActivity().getSupportFragmentManager().beginTransaction().
                    add(R.id.main_container, MainActivity.addGroupFragment, "addGroup").hide(MainActivity.addGroupFragment).commit();
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_left, R.animator.slide_right).hide(this).
                show(MainActivity.addGroupFragment).addToBackStack(null).commit();
        MainActivity.isGroupsFragmentsOnScreen = false;
        MainActivity.isAddGroupFragmentOnScreen = true;
        getActivity().findViewById(R.id.groupsToolbarTitle).setVisibility(View.GONE);
        getActivity().findViewById(R.id.addGroupToolbarTitle).setVisibility(View.VISIBLE);
    };


}
