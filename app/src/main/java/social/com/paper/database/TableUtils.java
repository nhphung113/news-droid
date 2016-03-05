package social.com.paper.database;

import android.provider.BaseColumns;

/**
 * Created by phung nguyen on 8/9/2015.
 */
public class TableUtils {

    public TableUtils() {
    }

    public static abstract class PAPER implements BaseColumns {
        public static final String TABLE_NAME = "Paper";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ICON = "Icon";
        public static final String COLUMN_DATE_FORMAT = "DateFormat";
        public static final String COLUMN_CHOOSE = "Choose";
        public static final String COLUMN_ACTIVE = "Active";
    }

    public static abstract class CATEGORY implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_PAPER_ID = "PaperId";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_RSS_LINK = "RssLink";
    }

    public static abstract class NEWS implements BaseColumns {
        public static final String TABLE_NAME = "News";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_SHORT_NEWS = "ShortNews";
        public static final String COLUMN_CATE_ID = "CateId";
        public static final String COLUMN_POSTED = "Posted";
        public static final String COLUMN_VIEWED = "Viewed";
        public static final String COLUMN_IMAGE = "Image";
        public static final String COLUMN_LINK = "Link";
        public static final String COLUMN_IMAGE_LINK = "ImageLink";
        public static final String COLUMN_CONTENT_HTML = "ContentHtml";
    }

    public static abstract class VARIABLES implements BaseColumns {
        public static final String TABLE_NAME = "Constant";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_VALUE = "Value";
    }

    public static abstract class SAVE_NEWS implements BaseColumns {
        public static final String TABLE_NAME = "SaveNews";
        public static final String COLUMN_NEWS_ID = "NewsId";
        public static final String COLUMN_CREATED_TIME = "CreatedTime";
    }
}