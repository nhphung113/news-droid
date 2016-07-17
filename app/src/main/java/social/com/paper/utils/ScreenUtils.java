package social.com.paper.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by phung on 7/17/2016.
 */
public class ScreenUtils {

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
}
