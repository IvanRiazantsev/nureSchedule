package database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import api.Group;

@Dao
public interface GroupDAO {

    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Query("SELECT * FROM `group` WHERE id = :id")
    Group getById(long id);

    @Query("SELECT * FROM `group` WHERE name = :name")
    Group getByName(String name);

    @Insert
    void insertGroup(Group group);

    @Update
    void updateGroup(Group group);

    @Delete
    void deleteGroup(Group group);

}
