package adapters;


import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.ivanriazantsev.nureschedule.App;
import com.ivanriazantsev.nureschedule.MainActivity;
import com.ivanriazantsev.nureschedule.R;
import com.google.android.material.snackbar.Snackbar;

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

    public void updateItem(Object object) {
        if (object instanceof Group) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i) instanceof Group && ((Group) mList.get(i)).getId().equals(((Group) object).getId())) {
                    mList.set(i, object);
                    notifyItemChanged(i);
                    return;
                }
            }
        } else {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i) instanceof Teacher && ((Teacher) mList.get(i)).getId().equals(((Teacher) object).getId())) {
                    mList.set(i, object);
                    notifyItemChanged(i);
                    return;
                }
            }
        }

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

    @Override
    public int getItemViewType(int position) {
        return position;
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

                        if (groupDAO.getById(((Group) mList.get(position)).getId()) != null
                                && groupDAO.getById(((Group) mList.get(position)).getId()).getName().equals(((Group) mList.get(position)).getName())) {
                            if (!groupDAO.isAddedByName(((Group) mList.get(position)).getName())) {
                                groupDAO.setAddedByName(true, ((Group) mList.get(position)).getName());
                            } else {
                                throw new SQLiteConstraintException();
                            }
                        } else {
                            groupDAO.insertGroup((Group) mList.get(position));
                            groupDAO.setAddedByName(true, ((Group) mList.get(position)).getName());
                        }
                        MainActivity.savedGroupsAdapter.addToList(mList.get(position));
                    } else if (mList.get(position) instanceof Teacher) {

                        if (teacherDAO.getById(((Teacher) mList.get(position)).getId()) != null
                                && teacherDAO.getById(((Teacher) mList.get(position)).getId()).getShortName().equals(((Teacher) mList.get(position)).getShortName())) {
                            if (!teacherDAO.isAddedByName(((Teacher) mList.get(position)).getShortName())) {
                                teacherDAO.setAddedByName(true, ((Teacher) mList.get(position)).getShortName());
                            } else {
                                throw new SQLiteConstraintException();
                            }
                        } else {
                            teacherDAO.insertTeacher((Teacher) mList.get(position));
                            teacherDAO.setAddedByName(true, ((Teacher) mList.get(position)).getShortName());
                        }
                        MainActivity.savedGroupsAdapter.clearList();
                        MainActivity.savedGroupsAdapter.setList(groupDAO.getAllAdded(), teacherDAO.getAllAdded());
                    }
                    MainActivity.savedGroupsPlaceholder.setVisibility(View.GONE);


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
