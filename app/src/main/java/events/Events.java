
package events;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import api.Group;
import api.Teacher;

public class Events {

    @SerializedName("time-zone")
    @Expose
    private String timeZone;
    @SerializedName("events")
    @Expose
    private List<Event> events = null;
    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;
    @SerializedName("teachers")
    @Expose
    private List<Teacher> teachers = null;
    @SerializedName("subjects")
    @Expose
    private List<Subject> subjects = null;
    @SerializedName("types")
    @Expose
    private List<Type> types = null;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public Subject getSubjectById(long id) {
        for (Subject subject : subjects) {
            if (subject.getId() == id)
                return subject;
        }
        return null;
    }

    public Type getTypeById(long id) {
        for (Type type : types) {
            if (type.getId() == id)
                return type;
        }
        return null;
    }

}
