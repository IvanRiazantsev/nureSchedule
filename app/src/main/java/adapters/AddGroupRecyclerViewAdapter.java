package adapters;


import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.MainActivity;
import com.example.ivanriazantsev.nureschedule.R;

import java.util.ArrayList;
import java.util.Collection;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import api.Teacher;
import api.Group;
import database.AppDatabase;
import database.GroupDAO;
import database.TeacherDAO;


public class AddGroupRecyclerViewAdapter extends RecyclerView.Adapter<AddGroupRecyclerViewAdapter.AddGroupViewHolder> {


    private List<Object> mList = new ArrayList<>();
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
    public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_teachers_recycler_item, parent, false);
        return new AddGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void filterList(List<Object> list) {
        mList = list;
        notifyDataSetChanged();
    }


    class AddGroupViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;


        @SuppressLint("RestrictedApi")
        public AddGroupViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameText);

            nameTextView.setOnClickListener(v -> {

                int position = getAdapterPosition();

                try {
                    if (mList.get(position) instanceof Group) {
                        ((Group) mList.get(position)).setRefreshDate("Не обновлялось");
                        groupDAO.insertGroup((Group) mList.get(position));
                    }
                    else if (mList.get(position) instanceof Teacher) {
                        ((Teacher) mList.get(position)).setRefreshDate("Не обновлялось");
                        teacherDAO.insertTeacher((Teacher) mList.get(position));
                    }
                    MainActivity.savedGroupsAdapter.clearList();
                    MainActivity.savedGroupsAdapter.setList(groupDAO.getAll(), teacherDAO.getAll());
                    MainActivity.savedGroupsAdapter.notifyDataSetChanged();
                    MainActivity.savedGroupsPlaceholder.setVisibility(View.GONE);
                    MainActivity.refreshGroupsFAB.setVisibility(View.VISIBLE);
                    MainActivity.deleteGroupsFAB.setVisibility(View.VISIBLE);

                    Toast.makeText(v.getContext(), "Добавлено", Toast.LENGTH_SHORT).show();
                } catch (SQLiteConstraintException e) {
                    Toast.makeText(v.getContext(), "Уже в сохраненных", Toast.LENGTH_SHORT).show();
                }
            });
        }


        public void bind(Object object) {
            if (object instanceof Group)
                nameTextView.setText(((Group) object).getName());
            else if (object instanceof Teacher)
                nameTextView.setText(((Teacher) object).getShortName());
            else
                throw new IllegalArgumentException();
        }

    }



}
