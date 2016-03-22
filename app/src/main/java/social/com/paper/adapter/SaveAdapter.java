package social.com.paper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.activity.SaveActivity;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.SaveNewsDto;
import social.com.paper.utils.HelpUtils;

/**
 * Created by phung nguyen on 8/8/2015.
 */
public class SaveAdapter extends BaseAdapter {

    Context context;
    ArrayList<SaveNewsDto> data;

    public SaveAdapter(Context context, ArrayList<SaveNewsDto> data) {
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
            view = inflater.inflate(R.layout.save_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        NewsDto news = data.get(position).getNewsDto();
        final SaveNewsDto saveNewsDto = data.get(position);

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveActivity.deleteSaveNews(saveNewsDto, context);
            }
        });

        String link = news.getLink().length() > 40 ? news.getLink().substring(0, 40) + "..." : news.getLink() + "...";

        Glide.with(context).load(news.getImageLink())
                .fitCenter().placeholder(R.drawable.ic_photos)
                .crossFade().into(holder.image);

        holder.textLink.setText(link);
        holder.textTitle.setText(news.getTitle());
        holder.textTime.setText("Đã lưu " + HelpUtils.MilliToTimeString(new Date().getTime() - saveNewsDto.getCreatedTime()));

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.ivSaveIcPaper) ImageView image;
        @Bind(R.id.tvLink) TextView textLink;
        @Bind(R.id.tvTime) TextView textTime;
        @Bind(R.id.tvTitle) TextView textTitle;
        @Bind(R.id.layoutDelete) LinearLayout layoutDelete;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
