package social.com.paper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import social.com.paper.R;

/**
 * Created by phung on 7/17/2016.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final FrameLayout.LayoutParams params;
    private String[] data;

    public CategoriesAdapter(String[] data, int width) {
        this.data = data;
        this.params = new FrameLayout.LayoutParams(width, width);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_category_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public FrameLayout main;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.layout_category_item_textCatName);
            main = (FrameLayout) view.findViewById(R.id.layout_category_item_layout_main);
            main.setLayoutParams(params);
        }
    }
}
