package adapters;

import android.view.View;
import android.widget.TextView;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import database.SubjectDAO;
import database.TypeDAO;
import events.Event;
import events.Events;
import events.Type;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class WeekSection extends StatelessSection {

    String date;
    List<Event> eventList;
    private SubjectDAO subjectDAO = App.getDatabase().subjectDAO();
    private TypeDAO typeDAO = App.getDatabase().typeDAO();

    public WeekSection(String date, List<Event> eventList) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.week_recycler_item)
                .headerResourceId(R.layout.section_header)
                .build());

        this.date = date;
        this.eventList = eventList;
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

        Event event = eventList.get(position);
        Type type = typeDAO.getById(event.getType());
        itemHolder.timeStart.setText(App.getHoursAndMinutesTimeFromUnix((long)event.getStartTime()));
        itemHolder.timeEnd.setText(App.getHoursAndMinutesTimeFromUnix((long)event.getEndTime()));
        itemHolder.eventName.setText(subjectDAO.getById(event.getSubjectId()).getTitle());
        itemHolder.eventRoom.setText(event.getAuditory());
        itemHolder.eventType.setText(type.getShortName());

        itemHolder.cardView.setCardBackgroundColor(App.eventsColors.get(type.getType()));

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



        public MyItemViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.weekCard);
            timeStart = itemView.findViewById(R.id.timeStart);
            timeEnd = itemView.findViewById(R.id.timeEnd);
            eventName = itemView.findViewById(R.id.weekEventName);
            eventType = itemView.findViewById(R.id.weekEventType);
            eventRoom = itemView.findViewById(R.id.weekEventRoom);


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
