package database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import api.Teacher;

@Dao
public interface TeacherDAO {

    @Query("SELECT * FROM teacher")
    List<Teacher> getAll();

    @Query("SELECT * FROM teacher WHERE id = :id")
    Teacher getById(long id);

    @Query("SELECT * FROM teacher WHERE shortName = :name")
    Teacher getByName(String name);

    @Query("UPDATE teacher SET refreshDate = :date WHERE shortName = :name")
    void updateTeacherRefreshDate(String date, String name);

    @Query("UPDATE teacher SET refreshDate = :date")
    void updateAllTeachersRefreshDates(String date);

    @Insert
    void insertTeacher(Teacher teacher);

    @Update
    void updateTeacher(Teacher teacher);

    @Delete
    void deleteTeacher(Teacher teacher);


}
