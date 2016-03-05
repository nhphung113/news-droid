package social.com.paper.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.adapter.PapersListAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.PaperDto;
import social.com.paper.dto.VariableDto;
import social.com.paper.fragment.NewsListFragment;
import social.com.paper.utils.Variables;

/**
 * Created by phung nguyen on 7/23/2015.
 */
public class MainActivity extends ActionBarActivity {

    @Bind(R.id.drawer_layout) DrawerLayout mDrawerPaperLayout;
    @Bind(R.id.list_slidermenu)  ListView lvDrawerPaperList;
    private ActionBarDrawerToggle mDrawerPaperToggle;

    private BaseAdapter adapterPaper;
    private int mPositionPaperCurrent;
    private ArrayAdapter<String> mSpinnerAdapter;
    public String[] mCategoriesString;

    private ArrayList<PaperDto> mPaperList = new ArrayList<>();
    private PaperDto mPaperCurrent;

    private boolean flagInitData = false;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        } else if (count != Variables.PAPERS.length) {
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

        adapterPaper = new PapersListAdapter(getApplicationContext(), mPaperList);
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
            String posCate = db.getVariableByName(Variables.KEY_CATEGORY_POSITION);
            if (posCate == "")
                db.insertVariable(new VariableDto(Variables.KEY_CATEGORY_POSITION, position + ""));
            else if (flagInitData && posCate != "") {
                int pos = Integer.parseInt(posCate);
                if (pos <= mPaperCurrent.getCategories().size()) {
                    position = pos;
                    db.updateVariable(new VariableDto(Variables.KEY_CATEGORY_POSITION, position + ""));
                }
            } else
                db.updateVariable(new VariableDto(Variables.KEY_CATEGORY_POSITION, position + ""));

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerPaperToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_add_papers) {
            Intent intent = new Intent(MainActivity.this, SourcePaperActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_saved_news) {
            Intent intent = new Intent(MainActivity.this, SaveActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_share) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.application_play_store));
            startActivity(Intent.createChooser(share, getResources().getString(R.string.action_share_news)));
            return true;
        } else if (id == R.id.action_rating) {
            String url = getResources().getString(R.string.application_play_store);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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