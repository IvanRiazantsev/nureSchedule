
package events;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import adapters.TeachersAndGroupsConverter;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Event {

    @PrimaryKey
    private Integer id;

    private String forGroup;


    @SerializedName("subject_id")
    @Expose
    private Integer subjectId;
    @SerializedName("start_time")
    @Expose
    private Integer startTime;
    @SerializedName("end_time")
    @Expose
    private Integer endTime;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("number_pair")
    @Expose
    private Integer numberPair;
    @SerializedName("auditory")
    @Expose
    private String auditory;

    @TypeConverters({TeachersAndGroupsConverter.class})
    @SerializedName("teachers")
    @Expose
    private List<Integer> teachers = null;

    @TypeConverters({TeachersAndGroupsConverter.class})
    @SerializedName("groups")
    @Expose
    private List<Integer> groups = null;

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getNumberPair() {
        return numberPair;
    }

    public void setNumberPair(Integer numberPair) {
        this.numberPair = numberPair;
    }

    public String getAuditory() {
        return auditory;
    }

    public void setAuditory(String auditory) {
        this.auditory = auditory;
    }

    public List<Integer> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Integer> teachers) {
        this.teachers = teachers;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getForGroup() {
        return forGroup;
    }

    public void setForGroup(String forGroup) {
        this.forGroup = forGroup;
    }
}
