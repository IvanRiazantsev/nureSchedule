package database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import api.Group;
import api.Teacher;

@Database(entities = {Group.class, Teacher.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GroupDAO groupDAO();
    public abstract TeacherDAO teacherDAO();
}
