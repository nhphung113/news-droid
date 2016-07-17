package social.com.paper.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import social.com.paper.R;

/**
 * Created by phung on 7/17/2016.
 */
public class CategoriesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_categories_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
