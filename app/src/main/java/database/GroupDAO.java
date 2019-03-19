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

    @Query("UPDATE `group` SET refreshDate = :date WHERE name = :name")
    void updateGroupRefreshDate(String date, String name);

    @Query("UPDATE `group` SET refreshDate = :date")
    void updateAllGroupsRefreshDates(String date);

    @Query("UPDATE `group` SET isSelected = :isSelected WHERE name = :name")
    void updateIsSelected(Boolean isSelected, String name);

    @Query("SELECT * FROM `group` WHERE isSelected = 1")
    Group getSelected();

    @Insert
    void insertGroup(Group group);


    @Update
    void updateGroup(Group group);

    @Delete
    void deleteGroup(Group group);

}
