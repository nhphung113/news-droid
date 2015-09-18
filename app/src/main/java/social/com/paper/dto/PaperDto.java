package social.com.paper.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by phung nguyen on 8/11/2015.
 */
public class PaperDto implements Serializable {
    private int Id;
    private String Name;
    private int Icon;
    private String DateFormat;
    private int Choose;
    private int Active;
    private ArrayList<CategoryDto> categories;

    public PaperDto() {

    }

    public ArrayList<CategoryDto> getCategories() {
        return categories;
    }

    public String[] getCategoriesString() {
        String[] arr = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            arr[i] = categories.get(i).getName();
        }
        return arr;
    }

    public void setCategories(ArrayList<CategoryDto> categories) {
        this.categories = categories;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }

    public int getChoose() {
        return Choose;
    }

    public void setChoose(int choose) {
        Choose = choose;
    }

    public String getDateFormat() {
        return DateFormat;
    }

    public void setDateFormat(String dateFormat) {
        DateFormat = dateFormat;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
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
}
