package adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.R;
import com.example.ivanriazantsev.nureschedule.SemesterFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
import database.SubjectDAO;
import database.TypeDAO;
import events.Event;
import events.Type;

public class SemesterRecyclerAdapter extends RecyclerView.Adapter<SemesterRecyclerAdapter.SemesterViewHolder> {


    private List<List<Event>> eventsLists;
    private AppDatabase database = App.getDatabase();
    private SubjectDAO subjectDAO = database.subjectDAO();
    private TypeDAO typeDAO = database.typeDAO();
    private List<List<Event>> eventsListCopy = new ArrayList<>();
    private EventDAO eventDAO = database.eventDAO();
    private GroupDAO groupDAO = database.groupDAO();


    ArrayMap<Event, List<Event>> simultaneousEvents;

    Date begin;
    Date end;



    public void setSimultaneousEvents(List<List<Event>> eventsLists, ArrayMap<Event, List<Event>> simultaneousEvents) {
        this.simultaneousEvents = simultaneousEvents;
        begin = App.getStartOfDay(App.getDateFromUnix((long) (eventDAO.getMinTimeForGroup(groupDAO.getSelected().getName()))));

        end = App.getStartOfNextDay(App.getStartOfDay(App.getDateFromUnix((long) (eventDAO.getMaxTimeForGroup(groupDAO.getSelected().getName())))));

        this.eventsLists = eventsLists;

        for (int i = 0; i < eventsLists.size(); i++) {
            List<Event> events = new ArrayList<>();
            for (Event event : eventsLists.get(i)) {
                events.add(event);
            }
            eventsListCopy.add(events);
        }
    }

    public void setList(List<List<Event>> list) {
//        eventsLists.addAll(list);
//        eventsListCopy.addAll(list);
//        notifyDataSetChanged();
    }

    public void clearList() {
        if (eventsLists != null) {
            eventsLists.clear();
            eventsListCopy.clear();
            simultaneousEvents.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.semester_recycler_item, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {

        List<Event> eventsForDay = eventsLists.get(position);

        holder.date.setText(App.getDateForSemester(begin.getTime() / 1000 + position * 86400));


        if (eventsForDay.size() != 0 && App.getDateForSemester((long) eventsForDay.get(0).getStartTime()).equals(App.getDateForSemester(new Date().getTime() / 1000))) {
            holder.date.setTextColor(Color.argb(255,116,55,165));
        }
        //TODO: empty days dates
        List<Event> eventsForDayCopy = eventsListCopy.get(position);


        for (int i = 0; i < eventsForDay.size(); i++) {
            Event event = eventsForDay.get(i);

            int size;


            switch (eventsForDay.get(i).getNumberPair()) {
                case 1:
                    holder.date.setText(App.getDateForSemester((long) (event.getStartTime())));
                    holder.card1.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event.getType()).getType()));
                    holder.name1.setText(subjectDAO.getById(event.getSubjectId()).getBrief());
                    holder.type1.setText(typeDAO.getById(typeDAO.getById(event.getType()).getId()).getShortName());
                    holder.room1.setText(event.getAuditory());
                    holder.card1.setVisibility(View.VISIBLE);
                    if (!simultaneousEvents.containsKey(event)) {
                        event = eventsForDayCopy.get(i);
                    }
                    size = simultaneousEvents.get(event).size();
                    if (size != 0) {
                        holder.nextAltButton1.setVisibility(View.VISIBLE);
                        Event finalEvent = event;
                        int finalI = i;
                        holder.nextAltButton1.setOnClickListener(v -> {
                            int current = -1;
                            for (int j = 0; j < size; j++) {
                                if (holder.room1.getText().toString().equals(simultaneousEvents.get(finalEvent).get(j).getAuditory())) {
                                    current = j;
                                    break;
                                }
                            }
                            if (current != size - 1) {
                                eventsLists.get(position).set(finalI, simultaneousEvents.get(finalEvent).get(current + 1));
                            } else {
                                eventsLists.get(position).set(finalI, finalEvent);
                            }

                            notifyItemChanged(position);
                        });
                    }
                    break;
                case 2:
                    holder.date.setText(App.getDateForSemester((long) (event.getStartTime())));
                    holder.card2.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event.getType()).getType()));
                    holder.name2.setText(subjectDAO.getById(event.getSubjectId()).getBrief());
                    holder.type2.setText(typeDAO.getById(typeDAO.getById(event.getType()).getId()).getShortName());
                    holder.room2.setText(event.getAuditory());
                    holder.card2.setVisibility(View.VISIBLE);
                    if (!simultaneousEvents.containsKey(event)) {
                        event = eventsForDayCopy.get(i);
                    }
                    size = simultaneousEvents.get(event).size();
                    if (size != 0) {
                        holder.nextAltButton2.setVisibility(View.VISIBLE);
                        Event finalEvent = event;
                        int finalI = i;
                        holder.nextAltButton2.setOnClickListener(v -> {
                            int current = -1;
                            for (int j = 0; j < size; j++) {
                                if (holder.room2.getText().toString().equals(simultaneousEvents.get(finalEvent).get(j).getAuditory())) {
                                    current = j;
                                    break;
                                }
                            }
                            if (current != size - 1) {
                                eventsLists.get(position).set(finalI, simultaneousEvents.get(finalEvent).get(current + 1));
                            } else {
                                eventsLists.get(position).set(finalI, finalEvent);
                            }

                            notifyItemChanged(position);
                        });
                    }
                    break;
                case 3:
                    holder.date.setText(App.getDateForSemester((long) (event.getStartTime())));
                    holder.card3.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event.getType()).getType()));
                    holder.name3.setText(subjectDAO.getById(event.getSubjectId()).getBrief());
                    holder.type3.setText(typeDAO.getById(typeDAO.getById(event.getType()).getId()).getShortName());
                    holder.room3.setText(event.getAuditory());
                    holder.card3.setVisibility(View.VISIBLE);
                    if (!simultaneousEvents.containsKey(event)) {
                        event = eventsForDayCopy.get(i);
                    }
                    size = simultaneousEvents.get(event).size();
                    if (size != 0) {
                        holder.nextAltButton3.setVisibility(View.VISIBLE);
                        Event finalEvent = event;
                        int finalI = i;
                        holder.nextAltButton3.setOnClickListener(v -> {
                            int current = -1;
                            for (int j = 0; j < size; j++) {
                                if (holder.room3.getText().toString().equals(simultaneousEvents.get(finalEvent).get(j).getAuditory())) {
                                    current = j;
                                    break;
                                }
                            }

                            if (current != size - 1) {
                                eventsLists.get(position).set(finalI, simultaneousEvents.get(finalEvent).get(current + 1));
                            } else {
                                eventsLists.get(position).set(finalI, finalEvent);
                            }

                            notifyItemChanged(position);
                        });
                    }
                    break;
                case 4:
                    holder.date.setText(App.getDateForSemester((long) (event.getStartTime())));
                    holder.card4.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event.getType()).getType()));
                    holder.name4.setText(subjectDAO.getById(eventsForDay.get(i).getSubjectId()).getBrief());
                    holder.type4.setText(typeDAO.getById(typeDAO.getById(event.getType()).getId()).getShortName());
                    holder.room4.setText(event.getAuditory());
                    holder.card4.setVisibility(View.VISIBLE);
                    if (!simultaneousEvents.containsKey(event)) {
                        event = eventsForDayCopy.get(i);
                    }
                    size = simultaneousEvents.get(event).size();
                    if (size != 0) {
                        holder.nextAltButton4.setVisibility(View.VISIBLE);
                        Event finalEvent = event;
                        int finalI = i;
                        holder.nextAltButton4.setOnClickListener(v -> {
                            int current = -1;
                            for (int j = 0; j < size; j++) {
                                if (holder.room4.getText().toString().equals(simultaneousEvents.get(finalEvent).get(j).getAuditory())) {
                                    current = j;
                                    break;
                                }
                            }
                            if (current != size - 1) {
                                eventsLists.get(position).set(finalI, simultaneousEvents.get(finalEvent).get(current + 1));
                            } else {
                                eventsLists.get(position).set(finalI, finalEvent);
                            }

                            notifyItemChanged(position);
                        });
                    }
                    break;
                case 5:
                    holder.date.setText(App.getDateForSemester((long) (event.getStartTime())));
                    holder.card5.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event.getType()).getType()));
                    holder.name5.setText(subjectDAO.getById(event.getSubjectId()).getBrief());
                    holder.type5.setText(typeDAO.getById(typeDAO.getById(event.getType()).getId()).getShortName());
                    holder.room5.setText(event.getAuditory());
                    holder.card5.setVisibility(View.VISIBLE);
                    if (!simultaneousEvents.containsKey(event)) {
                        event = eventsForDayCopy.get(i);
                    }
                    size = simultaneousEvents.get(event).size();
                    if (size != 0) {
                        holder.nextAltButton5.setVisibility(View.VISIBLE);
                        Event finalEvent = event;
                        int finalI = i;
                        holder.nextAltButton5.setOnClickListener(v -> {
                            int current = -1;
                            for (int j = 0; j < size; j++) {
                                if (holder.room5.getText().toString().equals(simultaneousEvents.get(finalEvent).get(j).getAuditory())) {
                                    current = j;
                                    break;
                                }
                            }
                            if (current != size - 1) {
                                eventsLists.get(position).set(finalI, simultaneousEvents.get(finalEvent).get(current + 1));
                            } else {
                                eventsLists.get(position).set(finalI, finalEvent);
                            }

                            notifyItemChanged(position);
                        });
                    }
                    break;
                case 6:
                    holder.date.setText(App.getDateForSemester((long) (event.getStartTime())));
                    holder.card6.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event.getType()).getType()));
                    holder.name6.setText(subjectDAO.getById(event.getSubjectId()).getBrief());
                    holder.type6.setText(typeDAO.getById(typeDAO.getById(event.getType()).getId()).getShortName());
                    holder.room6.setText(event.getAuditory());
                    holder.card6.setVisibility(View.VISIBLE);
                    if (!simultaneousEvents.containsKey(event)) {
                        event = eventsForDayCopy.get(i);
                    }
                    size = simultaneousEvents.get(event).size();
                    if (size != 0) {
                        holder.nextAltButton6.setVisibility(View.VISIBLE);
                        Event finalEvent = event;
                        int finalI = i;
                        holder.nextAltButton6.setOnClickListener(v -> {
                            int current = -1;
                            for (int j = 0; j < size; j++) {
                                if (holder.room6.getText().toString().equals(simultaneousEvents.get(finalEvent).get(j).getAuditory())) {
                                    current = j;
                                    break;
                                }
                            }

                            if (current != size - 1) {
                                eventsLists.get(position).set(finalI, simultaneousEvents.get(finalEvent).get(current + 1));
                            } else {
                                eventsLists.get(position).set(finalI, finalEvent);
                            }

                            notifyItemChanged(position);
                        });
                    }
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return eventsLists.size();
    }

    public class SemesterViewHolder extends RecyclerView.ViewHolder {

        private TextView date;

        private CardView card1;
        private TextView name1;
        private TextView type1;
        private TextView room1;
        private ImageButton nextAltButton1;

        private CardView card2;
        private TextView name2;
        private TextView type2;
        private TextView room2;
        private ImageButton nextAltButton2;


        private CardView card3;
        private TextView name3;
        private TextView type3;
        private TextView room3;
        private ImageButton nextAltButton3;


        private CardView card4;
        private TextView name4;
        private TextView type4;
        private TextView room4;
        private ImageButton nextAltButton4;


        private CardView card5;
        private TextView name5;
        private TextView type5;
        private TextView room5;
        private ImageButton nextAltButton5;


        private CardView card6;
        private TextView name6;
        private TextView type6;
        private TextView room6;
        private ImageButton nextAltButton6;


        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);

            card1 = itemView.findViewById(R.id.firstEvent);
            name1 = itemView.findViewById(R.id.name1);
            type1 = itemView.findViewById(R.id.type1);
            room1 = itemView.findViewById(R.id.room1);
            nextAltButton1 = itemView.findViewById(R.id.nextAltButton1);

            card2 = itemView.findViewById(R.id.secondEvent);
            name2 = itemView.findViewById(R.id.name2);
            type2 = itemView.findViewById(R.id.type2);
            room2 = itemView.findViewById(R.id.room2);
            nextAltButton2 = itemView.findViewById(R.id.nextAltButton2);

            card3 = itemView.findViewById(R.id.thirdEvent);
            name3 = itemView.findViewById(R.id.name3);
            type3 = itemView.findViewById(R.id.type3);
            room3 = itemView.findViewById(R.id.room3);
            nextAltButton3 = itemView.findViewById(R.id.nextAltButton3);

            card4 = itemView.findViewById(R.id.fourthEvent);
            name4 = itemView.findViewById(R.id.name4);
            type4 = itemView.findViewById(R.id.type4);
            room4 = itemView.findViewById(R.id.room4);
            nextAltButton4 = itemView.findViewById(R.id.nextAltButton4);

            card5 = itemView.findViewById(R.id.fifthEvent);
            name5 = itemView.findViewById(R.id.name5);
            type5 = itemView.findViewById(R.id.type5);
            room5 = itemView.findViewById(R.id.room5);
            nextAltButton5 = itemView.findViewById(R.id.nextAltButton5);

            card6 = itemView.findViewById(R.id.sixthEvent);
            name6 = itemView.findViewById(R.id.name6);
            type6 = itemView.findViewById(R.id.type6);
            room6 = itemView.findViewById(R.id.room6);
            nextAltButton6 = itemView.findViewById(R.id.nextAltButton6);

        }
    }
}
