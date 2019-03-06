
package faculties;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

}
