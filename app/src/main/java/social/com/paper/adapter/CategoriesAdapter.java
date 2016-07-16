package social.com.paper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;

/**
 * Created by phung nguyen on 7/22/2015.
 */
public class CategoriesAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private Activity context;
    private String[] data;

    public CategoriesAdapter(Activity context, String[] data) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
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
            view = inflater.inflate(R.layout.layout_category_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.textCatName.setText(data[position]);

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.layout_category_item_textCatName) TextView textCatName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
