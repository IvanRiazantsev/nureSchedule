package database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import events.Type;

@Dao
public interface TypeDAO {

    @Query("SELECT * FROM type")
    List<Type> getAll();

    @Query("SELECT * FROM type WHERE id = :id")
    Type getById(Integer id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertType(Type type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTypesList(List<Type> types);

    @Update
    void updateType(Type type);

    @Delete
    void deleteType(Type type);
}
