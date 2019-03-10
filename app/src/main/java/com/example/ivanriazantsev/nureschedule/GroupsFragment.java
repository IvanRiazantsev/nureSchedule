package com.example.ivanriazantsev.nureschedule;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;


public class GroupsFragment extends Fragment {

    View view;
    private FloatingActionButton addGroupButton;
    //private final Fragment addGroupFragment = new AddGroupFragment();


    public GroupsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups, container, false);

        //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_container, addGroupFragment, "addGroup").hide(addGroupFragment).commit();

        addGroupButton = view.findViewById(R.id.addGroupButton);

        addGroupButton.setOnClickListener(addGroupButtonListener);


        return view;
    }

    private View.OnClickListener addGroupButtonListener = v -> {
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_left, R.animator.slide_right).hide(this).
                show(MainActivity.addGroupFragment).addToBackStack(null).commit();
        MainActivity.isGroupsFragmentsOnScreen = false;
        MainActivity.isAddGroupFragmentOnScreen = true;
        getActivity().findViewById(R.id.groupsToolbarTitle).setVisibility(View.GONE);
        getActivity().findViewById(R.id.addGroupToolbarTitle).setVisibility(View.VISIBLE);
    };


}
