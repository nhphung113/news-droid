package social.com.paper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import social.com.paper.R;
import social.com.paper.activity.SaveActivity;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.SaveNewsDto;
import social.com.paper.fragment.NewsFragment;
import social.com.paper.utils.HelperUtils;

/**
 * Created by phung nguyen on 8/8/2015.
 */
public class SaveAdapter extends ArrayAdapter<SaveNewsDto> {

    Context context;
    int layoutId;
    ArrayList<SaveNewsDto> data;

    public SaveAdapter(Context context, int resource, ArrayList<SaveNewsDto> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutId, null);
        }
        final NewsDto newsDto = data.get(position).getNewsDto();
        final SaveNewsDto saveNewsDto = data.get(position);

        ImageView ivIc = (ImageView) convertView.findViewById(R.id.ivSaveIcPaper);
        TextView tvLink = (TextView) convertView.findViewById(R.id.tvSaveLink);
        TextView tvViewedTime = (TextView) convertView.findViewById(R.id.tvSaveTime);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvSaveTitle);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layoutCbDelete);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveActivity.deleteSaveNews(saveNewsDto, context);
            }
        });

        String link = newsDto.getLink().length() > 40 ? newsDto.getLink().substring(0, 40) + "..." : newsDto.getLink() + "...";
//        Bitmap bitmap = null;
//        if (newsDto.getImage()!= null)
//            bitmap = BitmapFactory.decodeByteArray(newsDto.getImage(), 0, newsDto.getImage().length);

        NewsFragment.mImageFetcher.loadImage(newsDto.getImageLink(), ivIc);

//        ivIc.setImageBitmap(bitmap);
        tvLink.setText(link);
        tvTitle.setText(newsDto.getTitle());
        long time = new Date().getTime();
        long time2 = time - saveNewsDto.getCreatedTime();
        tvViewedTime.setText("Đã lưu " + HelperUtils.MilliToTimeString(time2));
        return convertView;
    }
}
