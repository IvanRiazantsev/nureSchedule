package database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import api.Group;
import api.Teacher;
import events.Event;

@Database(entities = {Group.class, Teacher.class, Event.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GroupDAO groupDAO();
    public abstract TeacherDAO teacherDAO();
    public abstract EventDAO eventDAO();
}
