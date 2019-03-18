package database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import events.Subject;

@Dao
public interface SubjectDAO {

    @Query("SELECT * FROM subject")
    List<Subject> getAll();

    @Query("SELECT * FROM subject WHERE id = :id")
    Subject getById(Integer id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSubject(Subject subject);

    @Update
    void updateSubject(Subject subject);

    @Delete
    void deleteSubject(Subject subject);
}
