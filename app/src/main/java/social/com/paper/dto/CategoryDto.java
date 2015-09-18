package social.com.paper.dto;

import java.io.Serializable;

/**
 * Created by phung nguyen on 8/11/2015.
 */
public class CategoryDto implements Serializable {
    private int Id;
    private int PaperId;
    private String Name;
    private String RssLink;

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

    public int getPaperId() {
        return PaperId;
    }

    public void setPaperId(int paperId) {
        PaperId = paperId;
    }

    public String getRssLink() {
        return RssLink;
    }

    public void setRssLink(String rssLink) {
        RssLink = rssLink;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
