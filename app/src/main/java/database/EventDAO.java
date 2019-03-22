package database;

import java.util.Date;
import java.util.List;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import api.Group;
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

    @Query("SELECT * FROM event WHERE startTime >= :time AND forGroup = :group")
    List<Event> getEventsForGroupFromDate(int time, String group);


    @Query("SELECT min(startTime) FROM event WHERE forGroup = :group")
    Integer getMinTimeForGroup(String group);

    @Query("SELECT max(startTime) FROM event WHERE forGroup = :group")
    Integer getMaxTimeForGroup(String group);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEvent(Event event);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEventsList(List<Event> eventList);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
