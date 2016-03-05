package social.com.paper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import social.com.paper.R;
import social.com.paper.activity.SourcePaperActivity;
import social.com.paper.dto.PaperDto;

/**
 * Created by phung nguyen on 7/29/2015.
 */
public class SourcePapersAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PaperDto> data;

    public SourcePapersAdapter(Context context, ArrayList<PaperDto> list) {
        this.context = context;
        this.data = list;
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
        final ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_cource_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final PaperDto item = data.get(position);

        holder.image.setImageDrawable(context.getResources().getDrawable(item.getIcon()));
        holder.name.setText(item.getName());
        if (SourcePaperActivity.choosePaperMap.containsKey(item.getId()))
            holder.checkbox.setChecked(true);
        else
            holder.checkbox.setChecked(false);

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.checkbox.isChecked()) {
                    holder.checkbox.setChecked(false);
                    int p = 0;
                    for (int i = 0; i < SourcePaperActivity.choosePapers.size(); i++) {
                        if (SourcePaperActivity.choosePapers.get(i).getId() == item.getId()) {
                            p = i;
                            break;
                        }
                    }
                    SourcePaperActivity.choosePapers.remove(p);
                    SourcePaperActivity.choosePaperMap.remove(item.getId());
                } else {
                    holder.checkbox.setChecked(true);
                    SourcePaperActivity.choosePapers.add(item);
                    SourcePaperActivity.choosePaperMap.put(item.getId(), item.getId());
                }
            }
        });

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.image) ImageView image;
        @Bind(R.id.textName) TextView name;
        @Bind(R.id.checkPaper) CheckBox checkbox;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
