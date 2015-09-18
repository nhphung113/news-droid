package social.com.paper.dto;

import java.io.Serializable;

/**
 * Created by phung nguyen on 8/20/2015.
 */
public class SaveNewsDto implements Serializable {
    private int NewsId;
    private long CreatedTime;
    private NewsDto newsDto;

    public long getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(long createdTime) {
        CreatedTime = createdTime;
    }

    public NewsDto getNewsDto() {
        return newsDto;
    }

    public void setNewsDto(NewsDto newsDto) {
        this.newsDto = newsDto;
    }

    public int getNewsId() {
        return NewsId;
    }

    public void setNewsId(int newsId) {
        NewsId = newsId;
    }
}
