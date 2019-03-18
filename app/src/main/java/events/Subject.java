
package events;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Subject {


    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("brief")
    @Expose
    private String brief;
    @SerializedName("title")
    @Expose
    private String title;

    @Ignore
    @SerializedName("hours")
    @Expose
    private List<Hour> hours = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Hour> getHours() {
        return hours;
    }

    public void setHours(List<Hour> hours) {
        this.hours = hours;
    }

}
