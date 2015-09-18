package social.com.paper.utils;

import android.app.Activity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.model.RssItem;

/**
 * Created by phung nguyen on 7/23/2015.
 */
public class RssHandler extends DefaultHandler {
    public static final String ITEM = "item";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String DATE = "pubDate";
    public static final String SUMMARY_IMG = "summaryImg";
    public static final String IMAGE = "image";
    public static final String END_CODED = "encoded";

    private PaperDto mPaperDto;
    private int posCate;
    private NewsDto mNewsDto;
    private ArrayList<NewsDto> mNewsDtoList = new ArrayList<>();
    private Activity mActivity;

    private RssItem mRssItem;
    private Boolean started = false;
    private StringBuilder stringBuilder = new StringBuilder();
    private HashMap<String, String> mNewsMap = new HashMap<>();

    public ArrayList<NewsDto> getNewsList() {
        return mNewsDtoList;
    }

    public void setPaperDto(PaperDto paperDto) {
        this.mPaperDto = paperDto;
    }

    public void setPosCate(int posCate) {
        this.posCate = posCate;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (started && stringBuilder != null)
            stringBuilder.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equalsIgnoreCase(ITEM)) {
            mRssItem = new RssItem();
            mNewsDto = new NewsDto();
            mNewsDto.setCateId(mPaperDto.getCategories().get(posCate).getId());
            started = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equalsIgnoreCase(ITEM)) {
            if (!mNewsMap.containsKey(mRssItem.getTitle())) {
                if (mRssItem.getLink() != null && !mRssItem.getLink().contains("video.vnexpress.net")) {
                    mNewsMap.put(mRssItem.getTitle(), mRssItem.getTitle());
                    DatabaseHandler db = new DatabaseHandler(mActivity);
                    NewsDto newsDto = db.getNewsByLink(mRssItem.getLink());
                    if (newsDto != null)
                        mNewsDtoList.add(newsDto);
                    else {
                        mNewsDto.setCateId(mPaperDto.getCategories().get(posCate).getId());
                        mNewsDto.setTitle(mRssItem.getTitle());
                        mNewsDto.setImageLink(mRssItem.getImgSrc());
                        mNewsDto.setLink(mRssItem.getLink());
                        mNewsDto.setPostedDate(mRssItem.getPostedTime());
                        mNewsDto.setShortNews(mRssItem.getDescription());
                        int _id = db.insertNews(mNewsDto);
                        mNewsDto.setId(_id);
                        mNewsDtoList.add(mNewsDto);
                    }
                    mRssItem = new RssItem();
                    mNewsDto = new NewsDto();
                }
            }
        } else if (started) {
            if (!stringBuilder.toString().equalsIgnoreCase("")) {
                if (localName.equalsIgnoreCase(TITLE)) {
                    handleTitle(stringBuilder.toString());
                } else if (localName.equalsIgnoreCase(DESCRIPTION)) {
                    handleDescription(stringBuilder.toString());
                } else if (localName.equalsIgnoreCase(LINK)) {
                    mRssItem.setLink(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(DATE)) {
                    handlePubDate(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(SUMMARY_IMG)) {
                    mRssItem.setImgSrc(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(IMAGE)) {
                    mRssItem.setImgSrc(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(END_CODED)) {
                    handleImageFirst(stringBuilder.toString().trim());
                }
            }
            stringBuilder = new StringBuilder();
        }
    }

    private void handleTitle(String title) {
        title = title.replace("&apos;", "'");
        title = title.replace("amp;", "");
        title = title.replace("\\n", "").trim();
        mRssItem.setTitle(title);
    }

    private void handleImageFirst(String string_builder) {
        Document doc = Jsoup.parse("<p>" + string_builder + "</p>");
        Element imgElement = doc.select("img").first();
        if (imgElement != null) {
            mRssItem.setImgSrc(imgElement.attr("src"));
            doc.select("img").remove();
        }
    }

    private void handlePubDate(String string_builder) {
        Date pubDate;
        SimpleDateFormat sdf = new SimpleDateFormat(mPaperDto.getDateFormat(), Locale.US);
        try {
            pubDate = sdf.parse(string_builder);
        } catch (ParseException e) {
            e.printStackTrace();
            pubDate = null;
        }
        if (pubDate != null)
            mRssItem.setPostedTime(pubDate.getTime());
        else
            mRssItem.setPostedTime(new Date().getTime());
    }

    private void handleDescription(String string_builder) {
        if (string_builder.contains("thanhnien.com.vn")) {
            string_builder = string_builder.replace("&lt;", "<");
            string_builder = string_builder.replace("&quot;", "\"");
            string_builder = string_builder.replace("&apos;", "'");
            string_builder = string_builder.replace("&gt;", ">");
        }
        Document doc = Jsoup.parse("<p>" + string_builder + "</p>");
        Element imgElement = doc.select("img").first();
        if (imgElement != null) {
            mRssItem.setImgSrc(imgElement.attr("src"));
            doc.select("img").remove();
        }
        doc.select("a").remove();
        String short_news = "";
        Element shortNewsElement = doc.select("p").first();
        if (shortNewsElement != null) {
            short_news = shortNewsElement.toString();
            short_news = short_news.replace("<p>", "");
            short_news = short_news.replace("</p>", "");
            short_news = short_news.replace("</br>", "");
            short_news = short_news.replace("<br>", "");
            short_news = short_news.replace("</span>", "");
            short_news = short_news.replace("<span>", "");
            short_news = short_news.replace("<strong>", "");
            short_news = short_news.replace("</strong>", "");
            short_news = short_news.replace("<em>", " ");
            short_news = short_news.replace("</em>", "");
            short_news = short_news.replace("&nbsp;", "");
            short_news = short_news.replace("&amp;", "");
            short_news = short_news.replace("&#39;", "");
        }
        short_news = (short_news.length() > 200 ? short_news.substring(0, 200) : short_news);
        mRssItem.setDescription(short_news);
    }
}