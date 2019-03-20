package adapters;

import com.example.ivanriazantsev.nureschedule.App;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;
import database.AppDatabase;
import database.EventDAO;
import database.GroupDAO;
import events.Event;

public class SemesterPositionalDataSource extends PositionalDataSource<List<Event>> {

    private AppDatabase database = App.getDatabase();
    private EventDAO eventDAO = database.eventDAO();
    private GroupDAO groupDAO = database.groupDAO();


    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<List<Event>> callback) {
//        employeeStorage.getData(params.requestedStartPosition, params.requestedLoadSize);
        if (groupDAO.getSelected() != null) {
            List<Event> events = eventDAO.getEventsForGroupFromDate((int) (new Date().getTime() / 1000L), groupDAO.getSelected().getName());
//            for ()
//            List<List<Event>> result = eventDAO.getEventsForGroupFromDate((int) (new Date().getTime() / 1000L), groupDAO.getSelected().getName())
//            callback.onResult(result, 0);
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<List<Event>> callback) {

    }
}
