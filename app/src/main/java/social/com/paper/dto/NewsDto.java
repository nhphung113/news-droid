package social.com.paper.dto;

import java.io.Serializable;

/**
 * Created by phung nguyen on 8/11/2015.
 */
public class NewsDto implements Serializable {
    private int Id;
    private String Title;
    private String ShortNews;
    private String Link;
    private int CateId;
    private long PostedDate;
    private long ViewedDate;
    private byte Image[];
    private String ImageLink;
    private String ContentHtml;

    public String getContentHtml() {
        return ContentHtml;
    }

    public void setContentHtml(String contentHtml) {
        ContentHtml = contentHtml;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public int getCateId() {
        return CateId;
    }

    public void setCateId(int cateId) {
        CateId = cateId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public long getPostedDate() {
        return PostedDate;
    }

    public void setPostedDate(long postedDate) {
        PostedDate = postedDate;
    }

    public String getShortNews() {
        return ShortNews;
    }

    public void setShortNews(String shortNews) {
        ShortNews = shortNews;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public long getViewedDate() {
        return ViewedDate;
    }

    public void setViewedDate(long viewedDate) {
        ViewedDate = viewedDate;
    }
}
