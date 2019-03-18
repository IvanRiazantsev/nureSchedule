package database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import api.Group;
import api.Teacher;
import events.Event;
import events.Subject;
import events.Type;

@Database(entities = {Group.class, Teacher.class, Event.class, Subject.class, Type.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GroupDAO groupDAO();
    public abstract TeacherDAO teacherDAO();
    public abstract EventDAO eventDAO();
    public abstract SubjectDAO subjectDAO();
    public abstract TypeDAO typeDAO();
}
