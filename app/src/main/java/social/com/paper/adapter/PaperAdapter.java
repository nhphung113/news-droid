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

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.dto.PaperDto;

/**
 * Created by phung nguyen on 7/21/2015.
 */
public class PaperAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PaperDto> data;

    public PaperAdapter(Context context, ArrayList<PaperDto> data) {
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
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.paper_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.icon.setImageResource(data.get(position).getIcon());
        holder.title.setText(data.get(position).getName());

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.icon) ImageView icon;
        @Bind(R.id.title) TextView title;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
