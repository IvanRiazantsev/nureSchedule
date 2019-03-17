package adapters;

import android.view.View;
import android.widget.TextView;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import events.Event;
import events.Events;
import events.Type;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class WeekSectionAdapter extends StatelessSection {

    String date;
    List<Event> eventList;
    Events events;

    public WeekSectionAdapter(String date, List<Event> callList, Events events) {
        // call constructor with layout resources for this Section header, footer and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.week_recycler_item)
                .headerResourceId(R.layout.section_header)
                .build());

        this.date = date;
        this.eventList = callList;
        this.events = events;
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

        // bind your view here
        Event event = eventList.get(position);
        Type type = events.getTypeById(event.getType());
        itemHolder.timeStart.setText(App.getHoursAndMinutesTimeFromUnix(event.getStartTime() * 1000L));
        itemHolder.timeEnd.setText(App.getHoursAndMinutesTimeFromUnix(event.getEndTime() * 1000L));
        itemHolder.eventName.setText(events.getSubjectById(event.getSubjectId()).getTitle());
        itemHolder.eventRoom.setText(event.getAuditory());
        itemHolder.eventType.setText(type.getShortName());

        itemHolder.cardView.setCardBackgroundColor(App.eventsColors.get(type.getType()));

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return super.getHeaderViewHolder(view);
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
