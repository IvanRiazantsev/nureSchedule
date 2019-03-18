package adapters;


import android.annotation.SuppressLint;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.MainActivity;
import com.example.ivanriazantsev.nureschedule.R;
import com.example.ivanriazantsev.nureschedule.WeekFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import androidx.annotation.NonNull;


import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import api.Group;

import api.Teacher;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
import database.SubjectDAO;
import database.TeacherDAO;
import events.Event;
import events.Events;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedGroupsRecyclerViewAdapter extends RecyclerView.Adapter<SavedGroupsRecyclerViewAdapter.SavedGroupsViewHolder> {


    List<Object> mList = new ArrayList<>();
    private AppDatabase database = App.getDatabase();
    private GroupDAO groupDAO = database.groupDAO();
    private TeacherDAO teacherDAO = database.teacherDAO();
    private EventDAO eventDAO = database.eventDAO();


    public void setList(Collection<Group> groups, Collection<Teacher> teachers) {
        mList.addAll(groups);
        mList.addAll(teachers);
        notifyDataSetChanged();
    }

    public void clearList() {
        mList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SavedGroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_groups_recycler_item, parent, false);
        return new SavedGroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedGroupsViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SavedGroupsViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout savedGroupOrTeacher;
        private TextView name;
        private ImageButton settingsButton;
        private ImageButton removeButton;
        private TextView updateDate;
        private ImageButton refreshButton;


        @SuppressLint("RestrictedApi")
        public SavedGroupsViewHolder(@NonNull View itemView) {

            super(itemView);

            savedGroupOrTeacher = itemView.findViewById(R.id.savedGroup);
            name = itemView.findViewById(R.id.nameText);
            settingsButton = itemView.findViewById(R.id.settingsImageButton);
            removeButton = itemView.findViewById(R.id.removeImageButton);
            updateDate = itemView.findViewById(R.id.updateDateText);
            refreshButton = itemView.findViewById(R.id.refreshImageButton);


            removeButton.setOnClickListener(v -> {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(itemView.getContext());
                deleteDialog.setTitle("Удаление");
                deleteDialog.setMessage("Удалить выбранное расписание?");
                deleteDialog.setPositiveButton("Удалить", (dialog, which) -> {
                    int position = getAdapterPosition();

                    if (mList.get(position) instanceof Group) {
                        groupDAO.deleteGroup((Group) mList.get(position));
                    } else if (mList.get(position) instanceof Teacher) {
                        teacherDAO.deleteTeacher((Teacher) mList.get(position));
                    }
                    mList.remove(position);
                    itemView.animate().translationX(itemView.getWidth()).setDuration(100);

                    itemView.postDelayed(() -> notifyDataSetChanged(), 100);

                    if (mList.isEmpty()) {
                        MainActivity.savedGroupsPlaceholder.setVisibility(View.VISIBLE);
                        MainActivity.refreshGroupsFAB.setVisibility(View.GONE);
                    } else {
                        MainActivity.savedGroupsPlaceholder.setVisibility(View.GONE);
                        MainActivity.refreshGroupsFAB.setVisibility(View.VISIBLE);
                    }

                    eventDAO.deleteAllForGroup(name.getText().toString());
                    updateDate.setText("Не обновлялось");
                });
                deleteDialog.setNegativeButton("Отменить", null);
                deleteDialog.setCancelable(true);
                deleteDialog.show();


            });

            refreshButton.setOnClickListener(v -> {
                Object instance = mList.get(getAdapterPosition());

                Animation rotateAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.rotate);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
                refreshButton.startAnimation(rotateAnimation);
                ((MainActivity) MainActivity.savedGroupsPlaceholder.
                        getContext()).getWindow().
                        setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                App.getCistAPI().getEventsForGroup(
                        instance instanceof Group ? groupDAO.getById(((Group) instance).getId()).getId()
                                : teacherDAO.getById(((Teacher) instance).getId()).getId(),
                        instance instanceof Group ? 1 : 2, null, null, App.getKey()).enqueue(new Callback<Events>() {
                    @Override
                    public void onResponse(Call<Events> call, Response<Events> response) {
                        if (response.code() == 200) {
                            eventDAO.deleteAllForGroup(name.getText().toString());
                            List<Event> events = response.body().getEvents();
                            for (Event event : events) {
                                event.setForGroup(name.getText().toString());
                            }
                            eventDAO.insertEventsList(events);
                            String refreshDateString = "Обновлено " + App.getCurrentFullDate();
                            updateDate.setText(refreshDateString);
                            if (instance instanceof Group)
                                groupDAO.updateGroupRefreshDate(refreshDateString, name.getText().toString());
                            else
                                teacherDAO.updateTeacherRefreshDate(refreshDateString, name.getText().toString());
                            refreshButton.clearAnimation();
                            ((MainActivity) MainActivity.savedGroupsPlaceholder.
                                    getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Events> call, Throwable t) {
                        refreshButton.clearAnimation();
                        ((MainActivity) MainActivity.savedGroupsPlaceholder.
                                getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(itemView.getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            savedGroupOrTeacher.setOnClickListener(v -> {
                if (updateDate.getText().toString().equals("Не обновлялось")) {
                    Toast.makeText(itemView.getContext(), "Обновите расписание", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date currentDate = new Date();
                Date currentDay = App.getStartOfDay(currentDate);
                Date dayInAWeek = App.getStartOfDayInAWeek(currentDate);
                List<Event> eventsForWeek = eventDAO.getEventsBetweenTwoDatesForGroup(name.getText().toString(),
                        (int) (currentDay.getTime() / 1000L), (int) (dayInAWeek.getTime() / 1000L));

                List<Event> firstDay = new ArrayList<>();
                List<Event> secondDay = new ArrayList<>();
                List<Event> thirdDay = new ArrayList<>();
                List<Event> fourthDay = new ArrayList<>();
                List<Event> fifthDay = new ArrayList<>();
                List<Event> sixthDay = new ArrayList<>();
                List<Event> seventhDay = new ArrayList<>();

                Date[] dates = App.getWeek(currentDate);
                ArrayList<List<Event>> eventsByDaysList = new ArrayList<>();

                for (Event event : eventsForWeek) {

                    if (event.getStartTime() < dates[1].getTime() / 1000)
                        firstDay.add(event);
                    else if (event.getStartTime() < dates[2].getTime() / 1000)
                        secondDay.add(event);
                    else if (event.getStartTime() < dates[3].getTime() / 1000)
                        thirdDay.add(event);
                    else if (event.getStartTime() < dates[4].getTime() / 1000)
                        fourthDay.add(event);
                    else if (event.getStartTime() < dates[5].getTime() / 1000)
                        fifthDay.add(event);
                    else if (event.getStartTime() < dates[6].getTime() / 1000)
                        sixthDay.add(event);
                    else
                        seventhDay.add(event);
                }

                eventsByDaysList.add(0, firstDay);
                eventsByDaysList.add(1, secondDay);
                eventsByDaysList.add(2, thirdDay);
                eventsByDaysList.add(3, fourthDay);
                eventsByDaysList.add(4, fifthDay);
                eventsByDaysList.add(5, sixthDay);
                eventsByDaysList.add(6, seventhDay);

                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[0].getTime()), eventsByDaysList.get(0)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[1].getTime()), eventsByDaysList.get(1)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[2].getTime()), eventsByDaysList.get(2)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[3].getTime()), eventsByDaysList.get(3)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[4].getTime()), eventsByDaysList.get(4)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[5].getTime()), eventsByDaysList.get(5)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[6].getTime()), eventsByDaysList.get(6)));

                WeekFragment.weekRecyclerView.setAdapter(WeekFragment.sectionAdapter);

            });
        }

        public void bind(Object object) {
            if (object instanceof Group) {
                name.setText(((Group) object).getName());
                updateDate.setText(((Group) object).getRefreshDate());
            } else if (object instanceof Teacher) {
                name.setText(((Teacher) object).getShortName());
                updateDate.setText(((Teacher) object).getRefreshDate());
            } else
                throw new IllegalArgumentException();
        }
    }

}


