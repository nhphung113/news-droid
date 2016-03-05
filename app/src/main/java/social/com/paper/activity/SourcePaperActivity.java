package social.com.paper.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.adapter.SourcePapersAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.PaperDto;

/**
 * Created by phung nguyen on 7/29/2015.
 */
public class SourcePaperActivity extends ActionBarActivity implements Serializable {
    Menu menu;
    @Bind(R.id.listView) ListView listView;

    SourcePapersAdapter adapter;

    public static ArrayList<PaperDto> allPapers = new ArrayList<>();
    public static ArrayList<PaperDto> choosePapers = new ArrayList<>();
    public static HashMap<Integer, Integer> choosePaperMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_papers);
        ButterKnife.bind(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkPaper);
                PaperDto item = allPapers.get(position);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    int p = 0;
                    for (int i = 0; i < choosePapers.size(); i++) {
                        if (choosePapers.get(i).getId() == item.getId()) {
                            choosePapers.get(i).setActive(1);
                            p = i;
                            break;
                        }
                    }
                    choosePapers.remove(p);
                    choosePaperMap.remove(item.getId());
                } else {
                    checkBox.setChecked(true);
                    choosePapers.add(item);
                    choosePaperMap.put(item.getId(), item.getId());
                }
            }
        });

        setTitle(R.string.action_add_papers);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        allPapers = db.getAllSourcePapers();

        choosePapers = db.getPapersActive();
        for (int i = 0; i < choosePapers.size(); i++)
            choosePaperMap.put(choosePapers.get(i).getId(), choosePapers.get(i).getId());

        adapter = new SourcePapersAdapter(getApplicationContext(), allPapers);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        if (choosePapers.size()==0)
            Toast.makeText(getApplicationContext(), R.string.toast_source_news, Toast.LENGTH_SHORT).show();
        db.updatePatientActive(choosePapers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_add_all:
                addPaperAll();
                break;
            case R.id.menu_action_cancel_all:
                cancelPaperAll();
                break;
            case android.R.id.home:
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.updatePatientActive(choosePapers);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void emptyPaper() {
        choosePaperMap.clear();
        choosePapers.clear();
    }

    private void cancelPaperAll() {
        emptyPaper();
        menu.findItem(R.id.menu_action_add_all).setVisible(true);
        menu.findItem(R.id.menu_action_cancel_all).setVisible(false);
        adapter.notifyDataSetChanged();
    }

    private void addPaperAll() {
        emptyPaper();
        menu.findItem(R.id.menu_action_add_all).setVisible(false);
        menu.findItem(R.id.menu_action_cancel_all).setVisible(true);

        for (int i = 0; i < allPapers.size(); i++) {
            PaperDto paperDto = allPapers.get(i);
            paperDto.setActive(1);
            choosePapers.add(paperDto);
            choosePaperMap.put(paperDto.getId(), paperDto.getId());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_source_papers, menu);
        this.menu = menu;
        if (choosePapers.size() == allPapers.size()) {
            menu.findItem(R.id.menu_action_add_all).setVisible(false);
        } else {
            menu.findItem(R.id.menu_action_cancel_all).setVisible(false);
        }
        return true;
    }
}