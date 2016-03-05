package social.com.paper.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.nodes.Document;

/**
 * Created by phung nguyen on 8/4/2015.
 */
public class HelpUtils {

    public static String MilliToTimeString(long milliseconds) {
        String result = "";
        try {
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
        return result;
    }

    public static String getPaperContentHtml(Document document, String paperName) {
        String html = "";
        String[] contentKeys = Constant.PAPER_CONTENT_KEY[Constant.getPositionPaper(paperName)];
        for (int i = 0; i < contentKeys.length; i++) {
            if (html == "") {
                String isClass = contentKeys[i].substring(0, Constant.PAPER_CONTENT_KEY_GET.length());
                String content_key = contentKeys[i].substring(Constant.PAPER_CONTENT_KEY_GET.length());
                try {
                    if (isClass.equalsIgnoreCase(Constant.PAPER_CONTENT_KEY_DELETE))
                        document.select(content_key).first().remove();
                    else
                        html += document.select(content_key).first().html();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // special ccase.
        html = html.replace("<img alt=\"\" src=\"http://imgs.vietnamnet.vn/logo.gif\" class=\"logo-small\">- ", "");
        html = html.replace("//images.tienphong.vn", "http://images.tienphong.vn");
        return html;
    }

    public static boolean isConnectingToInternet(Context _context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
