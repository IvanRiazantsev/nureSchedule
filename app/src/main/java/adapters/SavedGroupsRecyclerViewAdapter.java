package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.GroupsFragment;
import com.example.ivanriazantsev.nureschedule.MainActivity;
import com.example.ivanriazantsev.nureschedule.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import api.Group;
import api.Main;
import api.Teacher;
import database.AppDatabase;
import database.GroupDAO;
import database.TeacherDAO;

public class SavedGroupsRecyclerViewAdapter extends RecyclerView.Adapter<SavedGroupsRecyclerViewAdapter.SavedGroupsViewHolder> {

    List<Object> mList = new ArrayList<>();
    private AppDatabase database = App.getDatabase();
    private GroupDAO groupDAO = database.groupDAO();
    private TeacherDAO teacherDAO = database.teacherDAO();


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


        private TextView name;
        private ImageButton settingsButton;
        private ImageButton removeButton;

        public SavedGroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameText);
            settingsButton = itemView.findViewById(R.id.settingsImageButton);
            removeButton = itemView.findViewById(R.id.removeImageButton);

            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (mList.get(position) instanceof Group) {
                    groupDAO.deleteGroup((Group) mList.get(position));
                } else if (mList.get(position) instanceof Teacher) {
                    teacherDAO.deleteTeacher((Teacher) mList.get(position));
                }
                mList.remove(position);
                notifyDataSetChanged();

                GroupsFragment fragment = (GroupsFragment) ((MainActivity) itemView.getContext()).getSupportFragmentManager().findFragmentByTag("groups");
                if (mList.isEmpty())
                    fragment.placeholder.setVisibility(View.VISIBLE);
                else
                    fragment.placeholder.setVisibility(View.GONE);

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
