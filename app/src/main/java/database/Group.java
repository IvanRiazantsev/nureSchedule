package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Group {

    @PrimaryKey
    public long id;

    public String name;

}

