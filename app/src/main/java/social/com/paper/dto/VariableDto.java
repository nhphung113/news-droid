package social.com.paper.dto;

/**
 * Created by phung nguyen on 8/17/2015.
 */
public class VariableDto {
    private int Id;
    private String Name;
    private String Value;

    public VariableDto(String name, String value) {
        Name = name;
        Value = value;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
