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

    @Query("DELETE FROM event WHERE forGroup = :group")
    void deleteAllForGroup(String group);

    @Query("SELECT * FROM event WHERE forGroup = :name AND startTime > :from AND startTime < :to")
    List<Event> getEventsBetweenTwoDatesForGroup(String name, Integer from, Integer to);

    @Insert
    void insertEvent(Event event);

    @Insert
    void insertEventsList(List<Event> eventList);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
