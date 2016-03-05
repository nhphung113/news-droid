package social.com.paper.task;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.utils.RssParser;

/**
 * Created by ST on 9/16/2015.
 */
public class LoadNewsListTask extends AsyncTask<Integer, Void, ArrayList<NewsDto>> {

    PaperDto mPaperDto;
    Activity mActivity;

    public LoadNewsListTask(PaperDto paperDto, Activity activity) {
        mPaperDto = paperDto;
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<NewsDto> doInBackground(Integer... params) {
        return RssParser.getNewsList(params[0], mPaperDto, mActivity);
    }

    @Override
    protected void onPostExecute(ArrayList<NewsDto> list) {
        super.onPostExecute(list);
    }
}
