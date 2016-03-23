package social.com.paper.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.adapter.PaperAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.dto.VariableDto;
import social.com.paper.fragment.NewsListFragment;
import social.com.paper.utils.Constant;

/**
 * Created by phung nguyen on 7/23/2015.
 */
public class MainActivity extends ActionBarActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerPaperLayout;
    @Bind(R.id.list_slidermenu)
    ListView lvDrawerPaperList;

    private BaseAdapter adapterPaper;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String[] mCategoriesString;

    private ArrayList<PaperDto> mPaperList = new ArrayList<>();
    private PaperDto mPaperCurrent;
    private int mPositionPaperCurrent;

    private boolean flagInitData = false;

    private ActionBarDrawerToggle mDrawerPaperToggle;
    private ActionBar mActionBar;

    public static ArrayList<NewsDto> newsCurrentLst = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        setupActionBar();
        setupData();
        eventControls();
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
    }

    private void setupData() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        int count = db.countPaper();
        if (count == 0) {
            Toast.makeText(getApplicationContext(), R.string.toast_init_data, Toast.LENGTH_LONG).show();
            db.initializeData();
        } else if (count != Constant.PAPERS.length) {
            getApplicationContext().deleteDatabase(DatabaseHandler.DATABASE_NAME);
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            Toast.makeText(getApplicationContext(), R.string.toast_init_data, Toast.LENGTH_LONG).show();
            db1.initializeData();
        }

        mPaperList = db.getPapersActive();
        for (int i = 0; i < mPaperList.size(); i++) {
            if (mPaperList.get(i).getChoose() == 1) {
                mPaperCurrent = mPaperList.get(i);
                mPositionPaperCurrent = i;
                break;
            }
        }

        if (mPositionPaperCurrent == 0) {
            mPaperCurrent = mPaperList.get(0);
            mPaperCurrent.setChoose(1);
            db.updatePatientChoose(mPaperCurrent);
        }

        adapterPaper = new PaperAdapter(getApplicationContext(), mPaperList);
        lvDrawerPaperList.setAdapter(adapterPaper);
        lvDrawerPaperList.setItemChecked(mPositionPaperCurrent, true);
        lvDrawerPaperList.setSelection(mPositionPaperCurrent);

        mCategoriesString = mPaperCurrent.getCategoriesString();
        mSpinnerAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, mCategoriesString);
        mActionBar.setListNavigationCallbacks(mSpinnerAdapter, new mOnNavigationListener());

        flagInitData = true;
    }

    public class mOnNavigationListener implements ActionBar.OnNavigationListener {
        @Override
        public boolean onNavigationItemSelected(int position, long itemId) {
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            String posCate = db.getVariableByName(Constant.KEY_CATEGORY_POSITION);
            if (posCate == "")
                db.insertVariable(new VariableDto(Constant.KEY_CATEGORY_POSITION, position + ""));
            else if (flagInitData && posCate != "") {
                int pos = Integer.parseInt(posCate);
                if (pos <= mPaperCurrent.getCategories().size()) {
                    position = pos;
                    db.updateVariable(new VariableDto(Constant.KEY_CATEGORY_POSITION, position + ""));
                }
            } else
                db.updateVariable(new VariableDto(Constant.KEY_CATEGORY_POSITION, position + ""));

            mActionBar.setSelectedNavigationItem(position);
            flagInitData = false;

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    NewsListFragment.newInstance(position, mPaperCurrent)).commit();

            return true;
        }
    }

    private void eventControls() {
        lvDrawerPaperList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPaperCurrent = mPaperList.get(position);
                mPaperCurrent.setChoose(1);
                mPositionPaperCurrent = position;
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.updatePatientChoose(mPaperCurrent);
                lvDrawerPaperList.setItemChecked(position, true);
                lvDrawerPaperList.setSelection(position);
                mDrawerPaperLayout.closeDrawer(lvDrawerPaperList);

                mCategoriesString = mPaperCurrent.getCategoriesString();
                mSpinnerAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, mCategoriesString);
                mActionBar.setListNavigationCallbacks(mSpinnerAdapter, new mOnNavigationListener());
            }
        });
        mDrawerPaperToggle = new ActionBarDrawerToggle(this, mDrawerPaperLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                mActionBar.setDisplayShowTitleEnabled(false);
                mActionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mActionBar.setTitle(mPaperCurrent.getName());
                mActionBar.setDisplayShowTitleEnabled(true);
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                invalidateOptionsMenu();
            }
        };
        mDrawerPaperLayout.setDrawerListener(mDrawerPaperToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerPaperToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_newss:
                saveNews();
                break;
            case R.id.action_add_papers:
                startActivity(new Intent(MainActivity.this, SourceActivity.class));
                break;
            case R.id.action_saved_news:
                startActivity(new Intent(MainActivity.this, SaveActivity.class));
                break;
            case R.id.action_share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.application_play_store));
                startActivity(Intent.createChooser(share, getResources().getString(R.string.action_share_news)));
                break;
            case R.id.action_rating:
                String url = getResources().getString(R.string.application_play_store);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.action_about:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.about, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Tải xuống tất cả tin hiện tại.");
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Đăng tải...", Toast.LENGTH_SHORT).show();
                new DownloadNewsAsyncTask(newsCurrentLst, new Date().getHours() + new Date().getMinutes() + new Date().getSeconds()).execute();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class DownloadNewsAsyncTask extends AsyncTask<Void, Integer, Integer> {

        private int id;
        private NotificationManager mNotifyManager;
        private Builder mBuilder;
        private ArrayList<NewsDto> data;

        public DownloadNewsAsyncTask(ArrayList<NewsDto> data, int id) {
            this.id = id;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new Builder(MainActivity.this);
            mBuilder.setContentTitle("NewsDroid").setContentText("Đang tải...");
            mBuilder.setProgress(100, 0, false);
            mBuilder.setAutoCancel(true);
            mBuilder.setSmallIcon(R.drawable.ic_file_download);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app));
            }
            mNotifyManager.notify(id, mBuilder.build());
        }

        public String getContent(Document document) {
            String html = "";
            String[] contentKeys = Constant.PAPER_CONTENT_KEY[Constant.getPositionPaper(mPaperCurrent.getName())];
            for (int i = 0; i < contentKeys.length; i++) {
                if (html.equalsIgnoreCase("")) {
                    String isClass = contentKeys[i].substring(0, Constant.PAPER_CONTENT_KEY_GET.length());
                    String content_key = contentKeys[i].substring(Constant.PAPER_CONTENT_KEY_GET.length());
                    try {
                        if (isClass.equalsIgnoreCase(Constant.PAPER_CONTENT_KEY_DELETE))
                            document.select(content_key).first().remove();
                        else if (isClass.equalsIgnoreCase(Constant.PAPER_CONTENT_TAG_KEY_DELETE))
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
        protected Integer doInBackground(Void... params) {
            int count = 0;
            for (int i = 0; i < data.size(); i++) {
                NewsDto newsDto = data.get(i);
                try {
                    String content = "";
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    if (!db.existsSaveNews(newsDto)) {
                        count++;
                        Document document = Jsoup.connect(newsDto.getLink()).get();
                        if (newsDto.getLink().contains("tinhte.vn")) {
                            content = document.html();
                        } else {
                            content = getContent(document);
                            content = content.replace("href", "hrefs");
                            content = "<html><head><style type='text/css'>body{text-align:justify;} img{width:100%25;} h1{text-align:left;} h2{text-align:left;} </style></head>"
                                    + "<body>" + content + "</body></html>";
                        }
                        newsDto.setContentHtml(content);
                        if (db.insertSaveNews(newsDto) != 0) {
                            int percent = (i + 1) * 100 / data.size();
                            publishProgress(percent);
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update progress
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            mBuilder.setContentText("Download hoàn tất");
            // Removes the progress bar
            mBuilder.setProgress(0, 0, false);
            mBuilder.setLights(Color.BLUE, 1000, 1000);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), SaveActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            mNotifyManager.notify(id, mBuilder.build());

            if (s == 0) {
                Toast.makeText(MainActivity.this, "Bạn đã download danh sách tin này.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Download hoàn tất (" + s + " tin)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mActionBar.setTitle(mPaperCurrent.getName());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerPaperToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerPaperToggle.onConfigurationChanged(newConfig);
    }
}