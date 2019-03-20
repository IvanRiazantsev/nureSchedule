package adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ivanriazantsev.nureschedule.App;
import com.example.ivanriazantsev.nureschedule.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import database.AppDatabase;
import database.SubjectDAO;
import database.TypeDAO;
import events.Event;
import events.Type;

public class SemesterRecyclerAdapter extends RecyclerView.Adapter<SemesterRecyclerAdapter.SemesterViewHolder> {


    private List<List<Event>> eventsLists = new ArrayList<>();
    private AppDatabase database = App.getDatabase();
    private SubjectDAO subjectDAO = database.subjectDAO();
    private TypeDAO typeDAO = database.typeDAO();

    public void setList(List<List<Event>> list) {
        eventsLists.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        eventsLists.clear();
        notifyDataSetChanged();
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
        if (eventsForDay.size() != 0 && App.getStartOfDay(new Date(eventsForDay.get(0).getStartTime() * 1000)).getTime() == App.getStartOfDay(new Date()).getTime())
            holder.date.setTextColor(Color.argb(255, 107, 14, 214));
        //TODO: empty days dates
        for (Event event1 : eventsForDay) {

            switch (event1.getNumberPair()) {
                case 1:
                    holder.date.setText(App.getDateForSemester((long) (event1.getStartTime())));
                    holder.card1.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event1.getType()).getType()));
                    holder.name1.setText(subjectDAO.getById(event1.getSubjectId()).getBrief());
                    holder.type1.setText(typeDAO.getById(typeDAO.getById(event1.getType()).getId()).getShortName());
                    holder.room1.setText(event1.getAuditory());
                    holder.card1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    holder.date.setText(App.getDateForSemester((long) (event1.getStartTime())));
                    holder.card2.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event1.getType()).getType()));
                    holder.name2.setText(subjectDAO.getById(event1.getSubjectId()).getBrief());
                    holder.type2.setText(typeDAO.getById(typeDAO.getById(event1.getType()).getId()).getShortName());
                    holder.room2.setText(event1.getAuditory());
                    holder.card2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    holder.date.setText(App.getDateForSemester((long) (event1.getStartTime())));
                    holder.card3.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event1.getType()).getType()));
                    holder.name3.setText(subjectDAO.getById(event1.getSubjectId()).getBrief());
                    holder.type3.setText(typeDAO.getById(typeDAO.getById(event1.getType()).getId()).getShortName());
                    holder.room3.setText(event1.getAuditory());
                    holder.card3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    holder.date.setText(App.getDateForSemester((long) (event1.getStartTime())));
                    holder.card4.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event1.getType()).getType()));
                    holder.name4.setText(subjectDAO.getById(event1.getSubjectId()).getBrief());
                    holder.type4.setText(typeDAO.getById(typeDAO.getById(event1.getType()).getId()).getShortName());
                    holder.room4.setText(event1.getAuditory());
                    holder.card4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    holder.date.setText(App.getDateForSemester((long) (event1.getStartTime())));
                    holder.card5.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event1.getType()).getType()));
                    holder.name5.setText(subjectDAO.getById(event1.getSubjectId()).getBrief());
                    holder.type5.setText(typeDAO.getById(typeDAO.getById(event1.getType()).getId()).getShortName());
                    holder.room5.setText(event1.getAuditory());
                    holder.card5.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    holder.date.setText(App.getDateForSemester((long) (event1.getStartTime())));
                    holder.card6.setCardBackgroundColor(App.eventsColors.get(typeDAO.getById(event1.getType()).getType()));
                    holder.name6.setText(subjectDAO.getById(event1.getSubjectId()).getBrief());
                    holder.type6.setText(typeDAO.getById(typeDAO.getById(event1.getType()).getId()).getShortName());
                    holder.room6.setText(event1.getAuditory());
                    holder.card6.setVisibility(View.VISIBLE);
                    break;
            }
        }
//        }


//        notifyDataSetChanged();

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

        private CardView card2;
        private TextView name2;
        private TextView type2;
        private TextView room2;

        private CardView card3;
        private TextView name3;
        private TextView type3;
        private TextView room3;

        private CardView card4;
        private TextView name4;
        private TextView type4;
        private TextView room4;

        private CardView card5;
        private TextView name5;
        private TextView type5;
        private TextView room5;

        private CardView card6;
        private TextView name6;
        private TextView type6;
        private TextView room6;


        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);

            card1 = itemView.findViewById(R.id.firstEvent);
            name1 = itemView.findViewById(R.id.name1);
            type1 = itemView.findViewById(R.id.type1);
            room1 = itemView.findViewById(R.id.room1);

            card2 = itemView.findViewById(R.id.secondEvent);
            name2 = itemView.findViewById(R.id.name2);
            type2 = itemView.findViewById(R.id.type2);
            room2 = itemView.findViewById(R.id.room2);

            card3 = itemView.findViewById(R.id.thirdEvent);
            name3 = itemView.findViewById(R.id.name3);
            type3 = itemView.findViewById(R.id.type3);
            room3 = itemView.findViewById(R.id.room3);

            card4 = itemView.findViewById(R.id.fourthEvent);
            name4 = itemView.findViewById(R.id.name4);
            type4 = itemView.findViewById(R.id.type4);
            room4 = itemView.findViewById(R.id.room4);

            card5 = itemView.findViewById(R.id.fifthEvent);
            name5 = itemView.findViewById(R.id.name5);
            type5 = itemView.findViewById(R.id.type5);
            room5 = itemView.findViewById(R.id.room5);

            card6 = itemView.findViewById(R.id.sixthEvent);
            name6 = itemView.findViewById(R.id.name6);
            type6 = itemView.findViewById(R.id.type6);
            room6 = itemView.findViewById(R.id.room6);

        }
    }
}
