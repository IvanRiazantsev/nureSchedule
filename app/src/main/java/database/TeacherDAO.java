package database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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

    @Query("UPDATE teacher SET isSelected = :isSelected WHERE shortName = :name")
    void updateIsSelected(Boolean isSelected, String name);

    @Query("SELECT * FROM teacher WHERE isSelected = 1")
    Teacher getSelected();

    @Query("SELECT * FROM teacher WHERE isAdded = 1 AND shortName = :name")
    Teacher getAddedByName(String name);

    @Query("SELECT isAdded FROM teacher WHERE shortName = :name")
    Boolean isAddedByName(String name);

    @Query("UPDATE teacher SET isAdded = :isAdded WHERE shortName = :name")
    void setAddedByName(Boolean isAdded, String name);


    @Query("SELECT * FROM teacher WHERE isAdded = 1")
    List<Teacher> getAllAdded();

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertTeachersList(List<Teacher> list);

    @Insert
    void insertTeacher(Teacher teacher);

    @Update
    void updateTeacher(Teacher teacher);

    @Delete
    void deleteTeacher(Teacher teacher);


}
