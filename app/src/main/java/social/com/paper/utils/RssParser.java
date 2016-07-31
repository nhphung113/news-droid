package social.com.paper.utils;

import android.app.Activity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;

/**
 * Created by phung nguyen on 7/23/2015.
 */
public class RssParser {
    public static ArrayList<NewsDto> getNewsList(Integer posCate, PaperDto paperDto, Activity activity) {
        ArrayList<NewsDto> result = new ArrayList<>();
        String link = paperDto.getCategories().get(posCate).getRssLink();
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(link);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            RssHandler handler = new RssHandler(activity, paperDto, posCate);
            reader.setContentHandler(handler);

            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(EntityUtils.toString(entity)));
            reader.parse(inStream);

            result = handler.getNews();
        } catch (Exception e) {
            try {
                URL url = new URL(link);
                InputSource input = new InputSource(url.openStream());
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();

                RssHandler handler = new RssHandler(activity, paperDto, posCate);

                XMLReader reader = parser.getXMLReader();
                reader.setContentHandler(handler);
                reader.parse(input);

                result = handler.getNews();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
