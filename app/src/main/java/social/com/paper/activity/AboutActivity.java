package social.com.paper.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import social.com.paper.R;

/**
 * Created by phung nguyen on 8/24/2015.
 */
public class AboutActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
    }
}
