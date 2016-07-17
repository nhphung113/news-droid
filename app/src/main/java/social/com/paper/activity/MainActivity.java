package social.com.paper.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.adapter.CategoriesAdapter;
import social.com.paper.adapter.PaperAdapter;
import social.com.paper.adapter.TabLayoutAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.dto.VariableDto;
import social.com.paper.ebus.HomeEvent;
import social.com.paper.fragment.CategoriesFragment;
import social.com.paper.fragment.NewsListFragment;
import social.com.paper.utils.Constant;

/**
 * Created by phung nguyen on 7/23/2015.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TAB_HOME_INDEX = 0;
    private static final int TAB_CATEGORIES_INDEX = 1;

    @Bind(R.id.listView)
    ListView listViewPaper;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private PaperAdapter adapterPaper;
    private CategoriesAdapter categoriesAdapter;
    private String[] mCategoriesString;

    private ArrayList<PaperDto> mPaperList = new ArrayList<>();
    private PaperDto mPaperCurrent;
    private int mPositionPaperCurrent;

    private boolean flagInitData = false;

    private ActionBarDrawerToggle mDrawerPaperToggle;
    private ActionBar mActionBar;

    public static ArrayList<NewsDto> newsCurrentLst = new ArrayList<>();
    private TabLayoutAdapter myFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initLayout();
        initData();
        initTabLayout();
        initEvents();
    }

    private void initEvents() {
        navigationView.setNavigationItemSelectedListener(this);
        listViewPaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                String posCate = db.getVariableByName(Constant.KEY_CATEGORY_POSITION);
                if (TextUtils.isEmpty(posCate))
                    db.insertVariable(new VariableDto(Constant.KEY_CATEGORY_POSITION, position + ""));
                else if (flagInitData) {
                    int pos = Integer.parseInt(posCate);
                    if (pos <= mPaperCurrent.getCategories().size()) {
                        position = pos;
                        db.updateVariable(new VariableDto(Constant.KEY_CATEGORY_POSITION, position + ""));
                    }
                } else {
                    db.updateVariable(new VariableDto(Constant.KEY_CATEGORY_POSITION, position + ""));
                }
                flagInitData = false;
                EventBus.getDefault().post(new HomeEvent(position, mPaperCurrent));
            }
        });
    }

    private void initTabLayout() {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        myFragmentPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager(), this);
        myFragmentPagerAdapter.addFragment(NewsListFragment.newInstance(0, mPaperCurrent));
        myFragmentPagerAdapter.addFragment(new CategoriesFragment());
        viewPager.setAdapter(myFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        setupTabView(tabLayout.getTabAt(TAB_HOME_INDEX), R.drawable.ic_tab_home);
        setupTabView(tabLayout.getTabAt(TAB_CATEGORIES_INDEX), R.drawable.ic_tab_categories);
    }

    private void setupTabView(TabLayout.Tab tab, int icon) {
        LinearLayout tabRoot = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        ImageView ivIcon = (ImageView) tabRoot.findViewById(R.id.custom_tab_layout_tab_icon);
        ivIcon.setImageResource(icon);
        if (tab.getPosition() == TAB_HOME_INDEX) ivIcon.setSelected(true);
        tab.setCustomView(tabRoot);
    }

    private void initLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initData() {
        DatabaseHandler db = new DatabaseHandler(this);
        int count = db.countPaper();
        if (count == 0) {
            Toast.makeText(this, R.string.toast_init_data, Toast.LENGTH_LONG).show();
            db.initializeData();
        } else if (count != Constant.PAPERS.length) {
            this.deleteDatabase(DatabaseHandler.DATABASE_NAME);
            DatabaseHandler db1 = new DatabaseHandler(this);
            Toast.makeText(this, R.string.toast_init_data, Toast.LENGTH_LONG).show();
            db1.initializeData();
        }

        ArrayList<PaperDto> mPaperList = db.getPapersActive();
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

        mPaperList = db.getPapersActive();
        adapterPaper = new PaperAdapter(this, mPaperList);
        listViewPaper.setAdapter(adapterPaper);
        flagInitData = true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
                View dialogView = getLayoutInflater().inflate(R.layout.layout_about, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void saveNews() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//        builder.setMessage("Tải xuống tất cả tin hiện tại.");
//        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "Đăng tải...", Toast.LENGTH_SHORT).show();
//                new DownloadNewsAsyncTask(newsCurrentLst, new Date().getHours() + new Date().getMinutes() + new Date().getSeconds()).execute();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

//    public class DownloadNewsAsyncTask extends AsyncTask<Void, Integer, Integer> {
//
//        private int id;
//        private NotificationManager mNotifyManager;
//        private Builder mBuilder;
//        private ArrayList<NewsDto> data;
//
//        public DownloadNewsAsyncTask(ArrayList<NewsDto> data, int id) {
//            this.id = id;
//            this.data = data;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new Builder(MainActivity.this);
//            mBuilder.setContentTitle("NewsDroid").setContentText("Đang tải...");
//            mBuilder.setProgress(100, 0, false);
//            mBuilder.setAutoCancel(true);
//            mBuilder.setSmallIcon(R.drawable.ic_file_download);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app));
//            }
//            mNotifyManager.notify(id, mBuilder.build());
//        }
//
//        public String getContent(Document document) {
//            String html = "";
//            String[] contentKeys = Constant.PAPER_CONTENT_KEY[Constant.getPositionPaper(mPaperCurrent.getName())];
//            for (int i = 0; i < contentKeys.length; i++) {
//                if (html.equalsIgnoreCase("")) {
//                    String isClass = contentKeys[i].substring(0, Constant.PAPER_CONTENT_KEY_GET.length());
//                    String content_key = contentKeys[i].substring(Constant.PAPER_CONTENT_KEY_GET.length());
//                    try {
//                        if (isClass.equalsIgnoreCase(Constant.PAPER_CONTENT_KEY_DELETE))
//                            document.select(content_key).first().remove();
//                        else if (isClass.equalsIgnoreCase(Constant.PAPER_CONTENT_TAG_KEY_DELETE))
//                            document.getElementsByTag(content_key).first().remove();
//                        else
//                            html += document.select(content_key).first().html();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            // special ccase.
//            html = html.replace("<img alt=\"\" src=\"http://imgs.vietnamnet.vn/logo.gif\" class=\"logo-small\">- ", "");
//            html = html.replace("//images.tienphong.vn", "http://images.tienphong.vn");
//            return html;
//        }
//
//        @Override
//        protected Integer doInBackground(Void... params) {
//            int count = 0;
//            for (int i = 0; i < data.size(); i++) {
//                NewsDto newsDto = data.get(i);
//                try {
//                    DatabaseHandler db = new DatabaseHandler(MainActivity.this);
//                    if (!db.existsSaveNews(newsDto)) {
//                        count++;
//                        Document document = Jsoup.connect(newsDto.getLink()).get();
//                        String content;
//                        if (newsDto.getLink().contains("tinhte.vn")) {
//                            content = document.html();
//                        } else {
//                            content = getContent(document);
//                            content = content.replace("href", "hrefs");
//                            content = "<html><head><style type='text/css'>body{text-align:justify;} img{width:100%25;} h1{text-align:left;} h2{text-align:left;} </style></head>"
//                                    + "<body>" + content + "</body></html>";
//                        }
//                        newsDto.setContentHtml(content);
//                        if (db.insertSaveNews(newsDto) != 0) {
//                            int percent = (i + 1) * 100 / data.size();
//                            publishProgress(percent);
//                        }
//                    }
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return count;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            // Update progress
//            mBuilder.setProgress(100, values[0], false);
//            mNotifyManager.notify(id, mBuilder.build());
//        }
//
//        @Override
//        protected void onPostExecute(Integer s) {
//            super.onPostExecute(s);
//            mBuilder.setContentText("Download hoàn tất");
//            // Removes the progress bar
//            mBuilder.setProgress(0, 0, false);
//            mBuilder.setLights(Color.BLUE, 1000, 1000);
//            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            mBuilder.setContentIntent(PendingIntent.getActivity(MainActivity.this, 0,
//                    new Intent(MainActivity.this, SaveActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
//            mNotifyManager.notify(id, mBuilder.build());
//
//            if (s == 0) {
//                Toast.makeText(MainActivity.this, "Bạn đã download danh sách tin này.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(MainActivity.this, "Download hoàn tất (" + s + " tin)", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}