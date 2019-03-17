package database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import events.Event;

@Dao
public interface EventDAO {

    @Query("SELECT * FROM event")
    List<Event> getAll();

    @Query("SELECT * FROM event WHERE id = :id")
    Event getById(long id);

    @Query("DELETE FROM event")
    void deleteAll();

//    @Query("DELETE FROM event WHERE groups")
//    void deleteAllForGroup(String group);

    @Insert
    void insertEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
