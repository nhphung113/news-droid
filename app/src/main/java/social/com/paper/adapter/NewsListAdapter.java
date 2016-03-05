package social.com.paper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.model.NewsItem;

/**
 * Created by phung nguyen on 7/22/2015.
 */
public class NewsListAdapter extends BaseAdapter {

    Activity context;
    ArrayList<NewsItem> data;

    public NewsListAdapter(Activity context, ArrayList<NewsItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_news_item_layout, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        NewsItem news = data.get(position);
        holder.textTitle.setText(news.getTitle());
        holder.textCatName.setText(news.getCategoryName());
        holder.textShort.setText(news.getShortNews());
        holder.textTime.setText(news.getTimeAgoString());
        Glide.with(context).load(news.getImageLink()).fitCenter().placeholder(R.drawable.ic_photos).crossFade().into(holder.image);

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.image) ImageView image;
        @Bind(R.id.textTitle) TextView textTitle;
        @Bind(R.id.textShort) TextView textShort;
        @Bind(R.id.textTime) TextView textTime;
        @Bind(R.id.textCatName) TextView textCatName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
