package adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.ivanriazantsev.nureschedule.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import api.Teacher;
import api.Group;


public class AddGroupRecyclerViewAdapter extends RecyclerView.Adapter<AddGroupRecyclerViewAdapter.AddGroupViewHolder> implements Filterable {


    private List<Object> mList = new ArrayList<>();
    private List<Object> mListFiltered = new ArrayList<>();

    //, Collection<Teacher> teachers
    public void setList(Collection<Group> groups) {
        mList.addAll(groups);
        //mList.addAll(teachers);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();
                if (str.isEmpty())
                    mListFiltered = mList;
                else {
                    List<Object> filteredList = new ArrayList<>();
                    for (Object object : mList) {
                        if (object instanceof Group) {
                            if (((Group) object).getName().toLowerCase().contains(str.toLowerCase())
                                    || ((Group) object).getName().toLowerCase().contains(str.toLowerCase().replace("і", "и")))
                                filteredList.add(object);
                        } else if (object instanceof Teacher) {
                            if (((Teacher) object).getShortName().toLowerCase().contains(str.toLowerCase())
                                    || ((Teacher) object).getShortName().toLowerCase().contains(str.toLowerCase().replace("і", "и")))
                                filteredList.add(object);
                        } else throw new IllegalArgumentException();
                    }
                    mListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListFiltered = (ArrayList<Object>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class AddGroupViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private ImageButton addImageButton;
        private ImageButton infoImageButton;

        public AddGroupViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameText);
            addImageButton = itemView.findViewById(R.id.addGroupButton);
            infoImageButton = itemView.findViewById(R.id.infoButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
