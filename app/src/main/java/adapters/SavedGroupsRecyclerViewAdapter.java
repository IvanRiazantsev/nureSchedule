package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.MainActivity;
import com.example.ivanriazantsev.nureschedule.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import api.Group;

import api.Teacher;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
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


        private LinearLayout savedGroup;
        private TextView name;
        private ImageButton settingsButton;
        private ImageButton removeButton;
        private TextView updateDate;
        private ImageButton refreshButton;
        private Timer mTimer;


        public SavedGroupsViewHolder(@NonNull View itemView) {
            super(itemView);

            savedGroup = itemView.findViewById(R.id.savedGroup);
            name = itemView.findViewById(R.id.nameText);
            settingsButton = itemView.findViewById(R.id.settingsImageButton);
            removeButton = itemView.findViewById(R.id.removeImageButton);
            updateDate = itemView.findViewById(R.id.updateDateText);
            refreshButton = itemView.findViewById(R.id.refreshImageButton);


            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (mList.get(position) instanceof Group) {
                    groupDAO.deleteGroup((Group) mList.get(position));
                } else if (mList.get(position) instanceof Teacher) {
                    teacherDAO.deleteTeacher((Teacher) mList.get(position));
                }
                mList.remove(position);
                itemView.animate().translationX(itemView.getWidth()).setDuration(100);

                itemView.postDelayed(() -> notifyDataSetChanged(), 100);

                if (mList.isEmpty())
                    MainActivity.savedGroupsPlaceholder.setVisibility(View.VISIBLE);
                else
                    MainActivity.savedGroupsPlaceholder.setVisibility(View.GONE);

            });

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventDAO.deleteAll();
                    App.getCistAPI().getEventsForGroup(groupDAO.getByName(name.getText().
                            toString()).getId(),null, null,App.getKey()).enqueue(new Callback<Events>() {
                        @Override
                        public void onResponse(Call<Events> call, Response<Events> response) {
                            if (response.code() == 200) {
                                List<Event> events = response.body().getEvents();
                                for (Event event : events) {
                                    eventDAO.insertEvent(event);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Events> call, Throwable t) {
                            Toast.makeText(itemView.getContext(),"Ошибка",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            savedGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                }
            });
        }

        public void bind(Object object) {
            if (object instanceof Group)
                name.setText(((Group) object).getName());
            else if (object instanceof Teacher)
                name.setText(((Teacher) object).getShortName());
            else
                throw new IllegalArgumentException();
        }
    }
}


