package social.com.paper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;

import social.com.paper.R;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.NewsDto;
import social.com.paper.model.NewsItem;
import social.com.paper.utils.HelperUtils;
import social.com.paper.utils.Variables;

/**
 * Created by phung nguyen on 7/23/2015.
 */
public class NewsDetailsActivity extends ActionBarActivity {
    WebView myWebView;
    NewsDto newsDto;
    NewsItem newsItem;
    String paperName;
    ProgressDialog dialog;
    long ticks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        ticks = (new Date().getTime());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.getSettings().setDefaultTextEncodingName("utf-8");

        newsDto = (NewsDto) getIntent().getExtras().getSerializable(Variables.KEY_SEND_NEWS_DTO);
        setTitle(newsDto.getTitle());

        newsItem = (NewsItem) getIntent().getExtras().getSerializable(Variables.KEY_SEND_NEWS_ITEM);
        paperName = getIntent().getStringExtra(Variables.KEY_SEND_PAPER_NAME);

        if (paperName != null) {
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            NewsDto _newsDto = db.getNewsByLink(newsDto.getLink());
            if (_newsDto != null && _newsDto.getContentHtml() != null)
                myWebView.loadData(_newsDto.getContentHtml(), "text/html; charset=utf-8", null);
            else {
                dialog = new ProgressDialog(this);
                dialog.setMessage(getResources().getString(R.string.toast_loading));
                dialog.show();
                new MyBrowserTask().execute(newsDto.getLink());
            }
        } else {
            myWebView.loadData(newsDto.getContentHtml(), "text/html; charset=utf-8", null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int _id = item.getItemId();
        if (_id == R.id.menu_action_save_news)
            saveNews();
        else if (_id == R.id.menu_action_share_news)
            shareNews(newsDto);
        return super.onOptionsItemSelected(item);
    }

    private void shareNews(NewsDto newsDto) {
        if (newsDto != null) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, newsDto.getTitle());
            share.putExtra(Intent.EXTRA_TEXT, newsDto.getLink());
            startActivity(Intent.createChooser(share, getResources().getString(R.string.toast_share_news_link)));
        } else
            Toast.makeText(getApplicationContext(), R.string.toast_dont_share_news_link, Toast.LENGTH_SHORT).show();
    }

    private void saveNews() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        if (db.existsSaveNews(newsDto))
            Toast.makeText(getApplicationContext(), R.string.toast_you_saved_this_news, Toast.LENGTH_SHORT).show();
        else {
            if (HelperUtils.isConnectingToInternet(getApplicationContext())) {
                if (db.insertSaveNews(newsDto) != 0)
                    Toast.makeText(getApplicationContext(), R.string.toast_saved_complete, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), R.string.toast_you_need_connent_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public class MyBrowserTask extends AsyncTask<String, Void, String> {
        Document document;

        public String getContent(Document document) {
            String html = "";
            String[] contentKeys = Variables.PAPER_CONTENT_KEY[Variables.getPositionPaper(paperName)];
            for (int i = 0; i < contentKeys.length; i++) {
                if (html.equalsIgnoreCase("")) {
                    String isClass = contentKeys[i].substring(0, Variables.PAPER_CONTENT_KEY_GET.length());
                    String content_key = contentKeys[i].substring(Variables.PAPER_CONTENT_KEY_GET.length());
                    try {
                        if (isClass.equalsIgnoreCase(Variables.PAPER_CONTENT_KEY_DELETE))
                            document.select(content_key).first().remove();
                        else if (isClass.equalsIgnoreCase(Variables.PAPER_CONTENT_TAG_KEY_DELETE))
                            document.getElementsByTag(content_key).first().remove();
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

        @Override
        protected String doInBackground(String... params) {
            String html = "";
            try {
                document = Jsoup.connect(params[0]).get();
                if (params[0].contains("tinhte.vn"))
                    return "";
                return getContent(document);
            } catch (IOException e) {
                document = null;
                e.printStackTrace();
            }
            return html;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String html) {
            super.onPostExecute(html);
            if (document != null) {
                if (html.equalsIgnoreCase(""))
                    html = document.html();
                else {
                    html = html.replace("href", "hrefs");
                    html = "<html><head><style type='text/css'>body{text-align:justify;} img{width:100%25;} h1{text-align:left;} h2{text-align:left;} </style></head>"
                            + "<body>" + html + "</body></html>";
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_sorry_we_dont_load_news, Toast.LENGTH_SHORT).show();
                finish();
            }
            newsDto.setContentHtml(html);
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            db.updateNewsContent(newsDto);

            myWebView.loadData(html, "text/html; charset=utf-8", null);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }
}