
package api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import api.Department;

public class Faculty {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("departments")
    @Expose
    private List<Department> departments = null;


    @SerializedName("directions")
    @Expose
    private List<Direction> directions = null;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
    }
}
