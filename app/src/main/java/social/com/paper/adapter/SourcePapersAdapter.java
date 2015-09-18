package social.com.paper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import social.com.paper.R;
import social.com.paper.activity.SourcePaperActivity;
import social.com.paper.dto.PaperDto;

/**
 * Created by phung nguyen on 7/29/2015.
 */
public class SourcePapersAdapter extends ArrayAdapter<PaperDto> {
    private Context context;
    private int layoutId;
    private ArrayList<PaperDto> list;

    public SourcePapersAdapter(Context context, int layout_id, ArrayList<PaperDto> list) {
        super(context, layout_id, list);
        this.context = context;
        this.layoutId = layout_id;
        this.list = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutId, null);
        }
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivUpdatePaper);
        TextView tvPaperName = (TextView) convertView.findViewById(R.id.tvUpdatePaperName);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.cbUpdatePaper);
        final PaperDto item = list.get(position);

        ivImage.setImageDrawable(context.getResources().getDrawable(item.getIcon()));
        tvPaperName.setText(item.getName());
        if (SourcePaperActivity.choosePaperMap.containsKey(item.getId()))
            cb.setChecked(true);
        else
            cb.setChecked(false);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb.isChecked()) {
                    cb.setChecked(false);
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
                    cb.setChecked(true);
                    SourcePaperActivity.choosePapers.add(item);
                    SourcePaperActivity.choosePaperMap.put(item.getId(), item.getId());
                }
            }
        });

        return convertView;
    }
}
