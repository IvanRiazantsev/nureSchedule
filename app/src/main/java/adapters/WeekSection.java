package adapters;

import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.MainActivity;
import com.example.ivanriazantsev.nureschedule.R;
import com.example.ivanriazantsev.nureschedule.WeekFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import database.GroupDAO;
import database.SubjectDAO;
import database.TeacherDAO;
import database.TypeDAO;
import events.Event;
import events.Type;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class WeekSection extends StatelessSection {

    String date;
    List<Event> eventList;
    List<Event> eventListCopy = new ArrayList<>();
    private SubjectDAO subjectDAO = App.getDatabase().subjectDAO();
    private TypeDAO typeDAO = App.getDatabase().typeDAO();
    private GroupDAO groupDAO = App.getDatabase().groupDAO();
    private TeacherDAO teacherDAO = App.getDatabase().teacherDAO();
    ArrayMap<Event, List<Event>> simultaneousEvents = new ArrayMap<>();


    public WeekSection(String date, List<Event> eventList) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.week_recycler_item)
                .headerResourceId(R.layout.section_header)
                .build());

        this.date = date;
        this.eventList = eventList;
        for (int i = 0; i < eventList.size(); i++) {
            Integer startTime = eventList.get(i).getStartTime();
            Event current = eventList.get(i);
            List<Event> events = new ArrayList<>();
            for (int j = i + 1; j < eventList.size(); j++) {
                if (startTime.equals(eventList.get(j).getStartTime())) {
                    events.add(eventList.get(j));
                    eventList.remove(j);
                    j--;
                }
            }
            simultaneousEvents.put(current, events);
        }
        eventListCopy.addAll(eventList);


    }


    @Override
    public int getContentItemsTotal() {
        return eventList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
        itemHolder.nextAlternativeButton.setVisibility(View.INVISIBLE);
        itemHolder.divider.setVisibility(View.INVISIBLE);

        Event event = eventList.get(position);
        Type type = typeDAO.getById(event.getType());
        itemHolder.timeStart.setText(App.getHoursAndMinutesTimeFromUnix((long) event.getStartTime()));
        itemHolder.timeEnd.setText(App.getHoursAndMinutesTimeFromUnix((long) event.getEndTime()));
        itemHolder.eventName.setText(subjectDAO.getById(event.getSubjectId()).getTitle());
        itemHolder.eventRoom.setText(event.getAuditory());
        itemHolder.eventType.setText(type.getShortName());

        itemHolder.cardView.setCardBackgroundColor(App.eventsColors.get(type.getType()));


        if (!simultaneousEvents.containsKey(event)) {
            event = eventListCopy.get(position);
        }


        int size = simultaneousEvents.get(event).size();


        if (size != 0) {
            itemHolder.nextAlternativeButton.setVisibility(View.VISIBLE);
            itemHolder.divider.setVisibility(View.VISIBLE);
        }


        Event finalEvent = event;
        itemHolder.nextAlternativeButton.setOnClickListener(v -> {
            int current = -1;
            for (int i = 0; i < size; i++) {
                if (itemHolder.eventRoom.getText().toString().equals(simultaneousEvents.get(finalEvent).get(i).getAuditory())) {
                    current = i;
                    break;
                }
            }
            if (current != size - 1) {
                eventList.set(position, simultaneousEvents.get(finalEvent).get(current + 1));

            } else {
                eventList.set(position, finalEvent);
            }

            WeekFragment.sectionAdapter.notifyItemChangedInSection(WeekSection.this, position);

        });

        itemHolder.cardView.setOnClickListener(v -> {

            MainActivity.bottomEventName.setText(subjectDAO.getById(eventList.get(position).getSubjectId()).getTitle());
            MainActivity.bottomRoom.setText(eventList.get(position).getAuditory());
            MainActivity.bottomType.setText(typeDAO.getById(eventList.get(position).getType()).getShortName());

            StringBuilder groups = new StringBuilder();
            for (Integer integer : eventList.get(position).getGroups()) {
                groups.append(groupDAO.getById(integer).getName()).append("\n");
            }
            groups.deleteCharAt(groups.lastIndexOf("\n"));
            MainActivity.bottomGroups.setText(groups.toString());

            StringBuilder teachers = new StringBuilder();
            if (eventList.get(position).getTeachers() != null) {
                for (Integer integer : eventList.get(position).getTeachers()) {
                    teachers.append(teacherDAO.getById(integer).getFullName()).append("\n");
                }
                teachers.deleteCharAt(teachers.lastIndexOf("\n"));
            }
            MainActivity.bottomTeacher.setText(teachers.toString());

            MainActivity.bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
            MainActivity.bottomSheetBehaviorAddGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);
            MainActivity.bottomSheetBehaviorInfo.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MyHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MyHeaderViewHolder headerHolder = (MyHeaderViewHolder) holder;

        headerHolder.weekDateText.setText(date);
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView timeStart;
        private TextView timeEnd;
        private TextView eventName;
        private TextView eventType;
        private TextView eventRoom;
        private ImageButton nextAlternativeButton;
        private View divider;


        public MyItemViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.weekCard);
            timeStart = itemView.findViewById(R.id.timeStart);
            timeEnd = itemView.findViewById(R.id.timeEnd);
            eventName = itemView.findViewById(R.id.weekEventName);
            eventType = itemView.findViewById(R.id.weekEventType);
            eventRoom = itemView.findViewById(R.id.weekEventRoom);
            nextAlternativeButton = itemView.findViewById(R.id.nextAlternativeSubjectButton);
            divider = itemView.findViewById(R.id.divider4);

        }

    }

    class MyHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView weekDateText;

        public MyHeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            weekDateText = itemView.findViewById(R.id.weekDateText);
        }
    }
}
