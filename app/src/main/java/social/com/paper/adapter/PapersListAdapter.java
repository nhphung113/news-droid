package social.com.paper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import social.com.paper.R;
import social.com.paper.dto.PaperDto;

/**
 * Created by phung nguyen on 7/21/2015.
 */
public class PapersListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PaperDto> papers;
    public PapersListAdapter(Context context, ArrayList<PaperDto> list)
    {
        this.context = context;
        this.papers = list;
    }
    @Override
    public int getCount() {
        return papers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_paper_list_item, null);
        }
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        imgIcon.setImageResource(papers.get(position).getIcon());
        txtTitle.setText(papers.get(position).getName());

        return convertView;
    }
}
