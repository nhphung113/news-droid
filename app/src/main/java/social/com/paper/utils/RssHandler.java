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

    private int position;
    private PaperDto paperDto;
    private NewsDto newsDto;
    private ArrayList<NewsDto> data = new ArrayList<>();
    private Activity activity;

    private RssItem rssItem;
    private Boolean started = false;
    private StringBuilder stringBuilder = new StringBuilder();
    private HashMap<String, String> newsMap = new HashMap<>();

    public RssHandler(Activity activity, PaperDto paperDto, int position) {
        this.activity = activity;
        this.paperDto = paperDto;
        this.position = position;
    }

    public ArrayList<NewsDto> getNews() {
        return data;
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
            rssItem = new RssItem();
            newsDto = new NewsDto();
            newsDto.setCateId(paperDto.getCategories().get(position).getId());
            started = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equalsIgnoreCase(ITEM)) {
            if (!newsMap.containsKey(rssItem.getTitle())) {
                if (rssItem.getLink() != null && !rssItem.getLink().contains("video.vnexpress.net")) {
                    newsMap.put(rssItem.getTitle(), rssItem.getTitle());
                    DatabaseHandler db = new DatabaseHandler(activity);
                    NewsDto newsDto = db.getNewsByLink(rssItem.getLink());
                    if (newsDto != null)
                        data.add(newsDto);
                    else {
                        this.newsDto.setCateId(paperDto.getCategories().get(position).getId());
                        this.newsDto.setTitle(rssItem.getTitle());
                        this.newsDto.setImageLink(rssItem.getImgSrc());
                        this.newsDto.setLink(rssItem.getLink());
                        this.newsDto.setPostedDate(rssItem.getPostedTime());
                        this.newsDto.setShortNews(rssItem.getDescription());
                        int _id = db.insertNews(this.newsDto);
                        this.newsDto.setId(_id);
                        data.add(this.newsDto);
                    }
                    rssItem = new RssItem();
                    this.newsDto = new NewsDto();
                }
            }
        } else if (started) {
            if (!stringBuilder.toString().equalsIgnoreCase("")) {
                if (localName.equalsIgnoreCase(TITLE)) {
                    handleTitle(stringBuilder.toString());
                } else if (localName.equalsIgnoreCase(DESCRIPTION)) {
                    handleDescription(stringBuilder.toString());
                } else if (localName.equalsIgnoreCase(LINK)) {
                    rssItem.setLink(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(DATE)) {
                    handlePubDate(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(SUMMARY_IMG)) {
                    rssItem.setImgSrc(stringBuilder.toString().trim());
                } else if (localName.equalsIgnoreCase(IMAGE)) {
                    rssItem.setImgSrc(stringBuilder.toString().trim());
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
        rssItem.setTitle(title);
    }

    private void handleImageFirst(String string_builder) {
        Document doc = Jsoup.parse("<p>" + string_builder + "</p>");
        Element imgElement = doc.select("img").first();
        if (imgElement != null) {
            rssItem.setImgSrc(imgElement.attr("src"));
            doc.select("img").remove();
        }
    }

    private void handlePubDate(String string_builder) {
        Date pubDate;
        SimpleDateFormat sdf = new SimpleDateFormat(paperDto.getDateFormat(), Locale.US);
        try {
            pubDate = sdf.parse(string_builder);
        } catch (ParseException e) {
            e.printStackTrace();
            pubDate = null;
        }
        if (pubDate != null)
            rssItem.setPostedTime(pubDate.getTime());
        else
            rssItem.setPostedTime(new Date().getTime());
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
            rssItem.setImgSrc(imgElement.attr("src"));
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
        rssItem.setDescription(short_news);
    }
}