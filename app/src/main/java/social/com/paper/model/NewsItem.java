package social.com.paper.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by phung nguyen on 7/24/2015.
 */
public class NewsItem implements Serializable, Parcelable {
    private String Title;
    private String ShortNews;
    private String CategoryName;
    private String PaperName;
    private String TimeAgoString;
    private String Link;
    private String ImageLink;
    private long TimeAgo;
    private Bitmap Image;

    public NewsItem() {
    }

    public NewsItem(Parcel in) {
        Title = in.readString();
        ShortNews = in.readString();
        CategoryName = in.readString();
        PaperName = in.readString();
        TimeAgoString = in.readString();
        Link = in.readString();
        ImageLink = in.readString();
        TimeAgo = in.readLong();
        Image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    public void setPaperName(String paperName) {
        PaperName = paperName;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        this.Link = link;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        this.ImageLink = imageLink;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.CategoryName = categoryName;
    }

    public String getTimeAgoString() {
        return TimeAgoString;
    }

    public void setDateTimeAgo(long input) {
        String result = "";
        try {
            long milliseconds = new Date().getTime() - input;
            long seconds = milliseconds / 1000;
            int minutes = (int) seconds / 60;
            int hours = minutes / 60;

            minutes = minutes - (hours * 60);
            seconds = seconds - (minutes * 60);
            if (hours > 0)
                if (hours > 24) {
                    int day = hours / 24;
                    hours = hours - day * 24;
                    result = day + " ngày " + hours + " giờ trước";
                } else
                    result = hours + " giờ " + minutes + " phút trước";
            else if (minutes > 0)
                result = minutes + " phút " + seconds + " giây trước";
            else
                result = seconds + " giây trước";
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.TimeAgoString = result;
    }

    public String getShortNews() {
        return ShortNews;
    }

    public void setShortNews(String shortNews) {
        this.ShortNews = shortNews;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeString(ShortNews);
        dest.writeString(CategoryName);
        dest.writeString(PaperName);
        dest.writeString(TimeAgoString);
        dest.writeString(Link);
        dest.writeString(ImageLink);
        dest.writeLong(TimeAgo);
        dest.writeParcelable(Image, flags);
    }
}
