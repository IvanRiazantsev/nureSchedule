package adapters;


import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.ArrayMap;
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
import com.example.ivanriazantsev.nureschedule.SemesterFragment;
import com.example.ivanriazantsev.nureschedule.WeekFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
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

import api.Main;
import api.Teacher;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
import database.SubjectDAO;
import database.TeacherDAO;
import database.TypeDAO;
import events.Event;
import events.Events;
import events.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedGroupsRecyclerViewAdapter extends RecyclerView.Adapter<SavedGroupsRecyclerViewAdapter.SavedGroupsViewHolder> {


    List<Object> mList = new ArrayList<>();
    private AppDatabase database = App.getDatabase();
    private GroupDAO groupDAO = database.groupDAO();
    private TeacherDAO teacherDAO = database.teacherDAO();
    private EventDAO eventDAO = database.eventDAO();
    private TypeDAO typeDAO = database.typeDAO();
    private SubjectDAO subjectDAO = database.subjectDAO();


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

                    //TODO: teacher
                    if (groupDAO.getSelected() != null && groupDAO.getSelected().getName()
                            .equals(mList.get(position) instanceof Group
                                    ? ((Group) mList.get(position)).getName() : ((Teacher) mList.get(position)).getShortName())) {
                        MainActivity.selectedScheduleName.setText("");
                        WeekFragment.sectionAdapter.removeAllSections();
                        WeekFragment.sectionAdapter.notifyDataSetChanged();
                        WeekFragment.weekPlaceholder.setVisibility(View.VISIBLE);
                    }
                    if (mList.get(position) instanceof Group) {
                        groupDAO.deleteGroup((Group) mList.get(position));
                    } else if (mList.get(position) instanceof Teacher) {
                        teacherDAO.deleteTeacher((Teacher) mList.get(position));
                    }
                    mList.remove(position);

                    notifyItemRemoved(position);

                    if (mList.isEmpty()) {
                        MainActivity.savedGroupsPlaceholder.setVisibility(View.VISIBLE);
                        MainActivity.refreshGroupsFAB.setVisibility(View.GONE);
                        MainActivity.deleteGroupsFAB.setVisibility(View.GONE);
                    } else {
                        MainActivity.savedGroupsPlaceholder.setVisibility(View.GONE);
                        MainActivity.refreshGroupsFAB.setVisibility(View.VISIBLE);
                        MainActivity.deleteGroupsFAB.setVisibility(View.VISIBLE);
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
                            typeDAO.insertTypesList(response.body().getTypes());
                            subjectDAO.insertSubjectsList(response.body().getSubjects());
                            eventDAO.deleteAllForGroup(name.getText().toString());
                            List<Event> events = response.body().getEvents();
                            for (Event event : events) {
                                event.setForGroup(name.getText().toString());
                            }
                            eventDAO.insertEventsList(events);
                            if (!updateDate.getText().toString().equals("Не обновлялось")) {
                                savedGroupOrTeacher.callOnClick();
                            }
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

                for (int i = 0; i < eventsForWeek.size(); i++) {
                    List<Integer> list = eventsForWeek.get(i).getGroups();
                    if (!list.contains(groupDAO.getByName(name.getText().toString()).getId())) {
                        eventsForWeek.remove(i);
                        i--;
                    }
                }

                List<Event> firstDay = new ArrayList<>();
                List<Event> secondDay = new ArrayList<>();
                List<Event> thirdDay = new ArrayList<>();
                List<Event> fourthDay = new ArrayList<>();
                List<Event> fifthDay = new ArrayList<>();
                List<Event> sixthDay = new ArrayList<>();
                List<Event> seventhDay = new ArrayList<>();

                Date[] dates = App.getWeek(currentDay);
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


                removeDuplicates(firstDay);
                removeDuplicates(secondDay);
                removeDuplicates(thirdDay);
                removeDuplicates(fourthDay);
                removeDuplicates(fifthDay);
                removeDuplicates(sixthDay);
                removeDuplicates(seventhDay);


                eventsByDaysList.add(0, firstDay);
                eventsByDaysList.add(1, secondDay);
                eventsByDaysList.add(2, thirdDay);
                eventsByDaysList.add(3, fourthDay);
                eventsByDaysList.add(4, fifthDay);
                eventsByDaysList.add(5, sixthDay);
                eventsByDaysList.add(6, seventhDay);

                WeekFragment.sectionAdapter.removeAllSections();
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[0].getTime()), eventsByDaysList.get(0)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[1].getTime()), eventsByDaysList.get(1)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[2].getTime()), eventsByDaysList.get(2)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[3].getTime()), eventsByDaysList.get(3)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[4].getTime()), eventsByDaysList.get(4)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[5].getTime()), eventsByDaysList.get(5)));
                WeekFragment.sectionAdapter.addSection(new WeekSection(App.getDateForWeek(dates[6].getTime()), eventsByDaysList.get(6)));


                WeekFragment.weekRecyclerView.setAdapter(WeekFragment.sectionAdapter);
                MainActivity.selectedScheduleName.setText(name.getText());
                MainActivity.selectedScheduleName.setVisibility(View.VISIBLE);
                MainActivity.bottomSheetBehaviorSavedGroups.setState(BottomSheetBehavior.STATE_COLLAPSED);

                WeekFragment.weekPlaceholder.setVisibility(View.GONE);


                if (groupDAO.getSelected() != null)
                    groupDAO.updateIsSelected(false, groupDAO.getSelected().getName());

                groupDAO.updateIsSelected(true, name.getText().toString());


                List<List<Event>> events = new ArrayList<>();

                Date begin = App.getStartOfDay(App.getDateFromUnix((long) (eventDAO.getMinTimeForGroup(groupDAO.getSelected().getName()))));

                Date end = App.getStartOfNextDay(App.getStartOfDay(App.getDateFromUnix((long) (eventDAO.getMaxTimeForGroup(groupDAO.getSelected().getName())))));

                ArrayMap<Event, List<Event>> simultaneousEvents = new ArrayMap<>();


                for (long i = begin.getTime(); i < end.getTime(); i += 86400000) {
                    List<Event> eventsForDay = eventDAO.getEventsBetweenTwoDatesForGroup(groupDAO.getSelected().getName(), (int) (i / 1000), (int) ((i + 86400000) / 1000));

                    for (int z = 0; z < eventsForDay.size(); z++) {
                        List<Integer> list = eventsForDay.get(z).getGroups();
                        if (!list.contains(groupDAO.getByName(name.getText().toString()).getId())) {
                            eventsForDay.remove(z);
                            z--;
                        }
                    }


                    removeDuplicates(eventsForDay);
                    for (int j = 0; j < eventsForDay.size(); j++) {
                        Integer startTime = eventsForDay.get(j).getStartTime();
                        Event current = eventsForDay.get(j);
                        List<Event> events1 = new ArrayList<>();
                        for (int q = j + 1; q < eventsForDay.size(); q++) {
                            if (startTime.equals(eventsForDay.get(q).getStartTime())) {
                                events1.add(eventsForDay.get(q));
                                eventsForDay.remove(q);
                                q--;
                            }
                        }
                        simultaneousEvents.put(current, events1);
                    }
                    events.add(eventsForDay);
                }

                SemesterFragment.semesterRecyclerAdapter.setSimultaneousEvents(events, simultaneousEvents);
                SemesterFragment.semesterRecyclerView.setItemAnimator(null);
                SemesterFragment.semesterRecyclerView.setAdapter(SemesterFragment.semesterRecyclerAdapter);
                SemesterFragment.semesterPlaceholder.setVisibility(View.GONE);

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

        void removeDuplicates(List<Event> list) {
            for (int i = 0; i < list.size(); i++) {
                if (i + 1 != list.size()) {
                    if (list.get(i).getSubjectId().equals(list.get(i + 1).getSubjectId())
                            && list.get(i).getNumberPair().equals(list.get(i + 1).getNumberPair())
                            && list.get(i).getType().equals(list.get(i + 1).getType())) {
                        list.get(i).setAuditory(list.get(i).getAuditory() + ", " + list.get(i + 1).getAuditory());
                        List<Integer> teachers = list.get(i).getTeachers();
                        if (list.get(i + 1).getTeachers() != null) {
                            teachers.addAll(list.get(i + 1).getTeachers());
                            list.get(i).setTeachers(teachers);
                        }
                        list.remove(i + 1);
                        i--;
                    }
                }
            }
        }
    }


}



