package social.com.paper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import social.com.paper.R;
import social.com.paper.fragment.NewsFragment;
import social.com.paper.model.NewsItem;

/**
 * Created by phung nguyen on 7/22/2015.
 */
public class NewsListAdapter extends ArrayAdapter<NewsItem> {

    Activity context;
    ArrayList<NewsItem> myNewsList;
    int layoutId;

    ImageView ivImage;

    public NewsListAdapter(Activity context, int layoutId, ArrayList<NewsItem> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myNewsList = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutId, null);
        }
        ivImage = (ImageView) convertView.findViewById(R.id.ivNews);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitleNews);
        TextView tvSortNews = (TextView) convertView.findViewById(R.id.tvShortNews);
        TextView tvTimeAgo = (TextView) convertView.findViewById(R.id.tvTimeAgo);
        TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);

        NewsItem newsItem = myNewsList.get(position);

        tvTitle.setText(newsItem.getTitle());
        tvCategoryName.setText(newsItem.getCategoryName());
        tvSortNews.setText(newsItem.getShortNews());
        tvTimeAgo.setText(newsItem.getTimeAgoString());

        NewsFragment.mImageFetcher.loadImage(newsItem.getImageLink(), ivImage);
        return convertView;
    }
}
