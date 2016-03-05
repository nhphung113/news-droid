package social.com.paper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.adapter.SaveAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.SaveNewsDto;
import social.com.paper.utils.Constant;

/**
 * Created by phung nguyen on 8/1/2015.
 */
public class SaveActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static SaveAdapter adapter;
    public static ArrayList<SaveNewsDto> newsList;
    public static ArrayList<SaveNewsDto> searchList;

    @Bind(R.id.lvSaved) ListView listView;
    @Bind(R.id.edtSavedKey) EditText editText;
    @Bind(R.id.ibSaveDelete) ImageButton imageButton;
    @Bind(R.id.saved_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save);
        ButterKnife.bind(this);

        setTitle("Tin đã lưu");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        newsList = db.getSaveNewsList();
        searchList = new ArrayList<>();

        adapter = new SaveAdapter(getApplicationContext(), newsList);
        listView.setAdapter(adapter);

        eventControls();
    }

    private void eventControls() {
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeRefreshLayout.setEnabled(true);
                else
                    swipeRefreshLayout.setEnabled(false);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                imageButton.setVisibility(View.INVISIBLE);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = editText.getText().toString();
                if (!value.equalsIgnoreCase("")) {
                    imageButton.setVisibility(View.VISIBLE);
                    searchList = new ArrayList<>();
                    for (int i = 0; i < newsList.size(); i++) {
                        if (newsList.get(i).getNewsDto().getTitle().contains(value))
                            searchList.add(newsList.get(i));
                    }
                    adapter = new SaveAdapter(getApplicationContext(), searchList);
                    listView.setAdapter(adapter);
                } else {
                    imageButton.setVisibility(View.INVISIBLE);
                    adapter = new SaveAdapter(getApplicationContext(), newsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SaveNewsDto saveNewsDto = null;
                if (searchList.size() == 0)
                    saveNewsDto = newsList.get(position);
                else
                    saveNewsDto = searchList.get(position);
                if (saveNewsDto != null) {
                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.KEY_SEND_NEWS_DTO, saveNewsDto.getNewsDto());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_delete_all:
                deleteSaveAll();
                editText.setText("");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSaveAll() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        if (searchList.size() == 0) {
            if (db.countSaveNews() != 0) {
                if (db.deleteAllSaveNews() == 1) {
                    newsList.clear();
                    adapter = new SaveAdapter(getApplicationContext(), newsList);
                    listView.setAdapter(adapter);
                    Toast.makeText(getApplicationContext(), R.string.toast_delete_all_news, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), R.string.toast_delete_news_failed, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), R.string.toast_no_save_news, Toast.LENGTH_SHORT).show();
        } else {
            int count = 0;
            for (int i = 0; i < searchList.size(); i++) {
                newsList.remove(searchList.get(i));
                db = new DatabaseHandler(getApplicationContext());
                if (db.deleteSaveNews(searchList.get(i)) == 1)
                    count++;
            }
            Toast.makeText(getApplicationContext(), "Đã xóa " + count + " tin", Toast.LENGTH_SHORT).show();
            searchList.clear();
            adapter = new SaveAdapter(getApplicationContext(), newsList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        newsList = db.getSaveNewsList();
        adapter = new SaveAdapter(getApplicationContext(), newsList);
        listView.setAdapter(adapter);
        editText.setText("");
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        newsList = db.getSaveNewsList();
        adapter.notifyDataSetChanged();

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    public static void deleteSaveNews(SaveNewsDto saveNewsDto, Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        if (db.deleteSaveNews(saveNewsDto) == 1) {
            searchList.remove(saveNewsDto);
            newsList.remove(saveNewsDto);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, R.string.toast_deleted_news, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, R.string.toast_delete_news_failed, Toast.LENGTH_SHORT).show();
    }
}

