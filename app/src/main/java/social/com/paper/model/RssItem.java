package social.com.paper.model;

/**
 * Created by phung nguyen on 7/24/2015.
 */
public class RssItem {
    private String Paper;
    private String Title;
    private String Link;
    private String Description;
    private String PostedDate;
    private String ImgSrc;
    private long PostedTime;

    public long getPostedTime() {
        return PostedTime;
    }

    public void setPostedTime(long postedTime) {
        this.PostedTime = postedTime;
    }

    public String getImgSrc() {
        return ImgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.ImgSrc = imgSrc;
    }

    public String getPaper() {
        return Paper;
    }

    public void setPaper(String paper) {
        this.Paper = paper;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        this.Link = link;
    }

    public String getPostedDate() {
        return PostedDate;
    }

    public void setPostedDate(String postedDate) {
        this.PostedDate = postedDate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }
}
