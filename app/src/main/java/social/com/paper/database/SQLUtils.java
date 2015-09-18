package social.com.paper.database;

import social.com.paper.database.TableUtils.*;

/**
 * Created by phung nguyen on 8/9/2015.
 */
public class SQLUtils {
    public static final String INT_TYPE = " INTEGER";
    public static final String LONG_TYPE = " LONG";
    public static final String TEXT_TYPE = " TEXT";
    public static final String BLOB_TYPE = " BLOB"; //byte[]
    public static final String COMMA_SEP = ", ";

    public static final String SQL_CREATE_PAPER = "CREATE TABLE "
            + PAPER.TABLE_NAME + " ("
            + PAPER._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PAPER.COLUMN_ID + INT_TYPE + COMMA_SEP
            + PAPER.COLUMN_NAME + TEXT_TYPE + COMMA_SEP
            + PAPER.COLUMN_ICON + INT_TYPE + COMMA_SEP
            + PAPER.COLUMN_CHOOSE + INT_TYPE + COMMA_SEP
            + PAPER.COLUMN_ACTIVE + INT_TYPE + COMMA_SEP
            + PAPER.COLUMN_DATE_FORMAT + TEXT_TYPE + ")";

    public static final String SQL_CREATE_CATEGORY = "CREATE TABLE "
            + CATEGORY.TABLE_NAME + " ("
            + CATEGORY._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CATEGORY.COLUMN_ID + INT_TYPE + COMMA_SEP
            + CATEGORY.COLUMN_NAME + TEXT_TYPE + COMMA_SEP
            + CATEGORY.COLUMN_RSS_LINK + TEXT_TYPE + COMMA_SEP
            + CATEGORY.COLUMN_PAPER_ID + INT_TYPE + ")";

    public static final String SQL_CREATE_NEWS = "CREATE TABLE "
            + NEWS.TABLE_NAME + " ("
            + NEWS._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NEWS.COLUMN_ID + INT_TYPE + COMMA_SEP
            + NEWS.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP
            + NEWS.COLUMN_SHORT_NEWS + TEXT_TYPE + COMMA_SEP
            + NEWS.COLUMN_CATE_ID + INT_TYPE + COMMA_SEP
            + NEWS.COLUMN_POSTED + LONG_TYPE + COMMA_SEP
            + NEWS.COLUMN_VIEWED + LONG_TYPE + COMMA_SEP
            + NEWS.COLUMN_LINK + TEXT_TYPE + COMMA_SEP
            + NEWS.COLUMN_IMAGE_LINK + TEXT_TYPE + COMMA_SEP
            + NEWS.COLUMN_CONTENT_HTML + TEXT_TYPE + COMMA_SEP
            + NEWS.COLUMN_IMAGE + BLOB_TYPE + ")";

    public static final String SQL_CREATE_VARIABLES = "CREATE TABLE "
            + VARIABLES.TABLE_NAME + " ("
            + VARIABLES._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + VARIABLES.COLUMN_ID + INT_TYPE + COMMA_SEP
            + VARIABLES.COLUMN_NAME + TEXT_TYPE + COMMA_SEP
            + VARIABLES.COLUMN_VALUE + TEXT_TYPE + ")";

    public static final String SQL_CREATE_SAVE_NEWS = "CREATE TABLE "
            + SAVE_NEWS.TABLE_NAME + " ("
            + SAVE_NEWS.COLUMN_NEWS_ID + " INTEGER PRIMARY KEY,"
            + SAVE_NEWS.COLUMN_CREATED_TIME + LONG_TYPE + ")";

    public static final String SQL_DELETE_PAPER = "DROP TABLE IF EXISTS " + PAPER.TABLE_NAME;
    public static final String SQL_DELETE_CATEGORY = "DROP TABLE IF EXISTS " + CATEGORY.TABLE_NAME;
    public static final String SQL_DELETE_NEWS = "DROP TABLE IF EXISTS " + NEWS.TABLE_NAME;
    public static final String SQL_DELETE_VARIABLES = "DROP TABLE IF EXISTS " + VARIABLES.TABLE_NAME;
    public static final String SQL_DELETE_SAVE_NEWS = "DROP TABLE IF EXISTS " + SAVE_NEWS.TABLE_NAME;
}
