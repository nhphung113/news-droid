package social.com.paper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import social.com.paper.database.TableUtils.*;
import social.com.paper.dto.CategoryDto;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.dto.SaveNewsDto;
import social.com.paper.dto.VariableDto;
import social.com.paper.utils.Constant;

/**
 * Created by phung nguyen on 8/11/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NewsDroid.db";
    private static final String LOG = "DatabaseHelper";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLUtils.SQL_CREATE_PAPER);
        db.execSQL(SQLUtils.SQL_CREATE_CATEGORY);
        db.execSQL(SQLUtils.SQL_CREATE_NEWS);
        db.execSQL(SQLUtils.SQL_CREATE_VARIABLES);
        db.execSQL(SQLUtils.SQL_CREATE_SAVE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQLUtils.SQL_DELETE_PAPER);
        db.execSQL(SQLUtils.SQL_DELETE_CATEGORY);
        db.execSQL(SQLUtils.SQL_DELETE_NEWS);
        db.execSQL(SQLUtils.SQL_DELETE_VARIABLES);
        db.execSQL(SQLUtils.SQL_DELETE_SAVE_NEWS);
        onCreate(db);
    }

    public void initializeData() {
        for (int i = 0; i < Constant.PAPERS.length; i++) {
            PaperDto paperDto = new PaperDto();
            paperDto.setName(Constant.PAPERS[i]);
            paperDto.setIcon(Constant.LOGOS[i]);
            paperDto.setDateFormat(Constant.FORMAT_DATE[i]);
            paperDto.setChoose((i == 0 ? 1 : 0));
            paperDto.setActive(Constant.isDefaultPaper(Constant.PAPERS[i]));
            int paper_id = insertPaper(paperDto);

            String[] categories = Constant.CATEGORIES[i];
            String[] links = Constant.LINKS[i];

            for (int j = 0; j < categories.length; j++) {
                CategoryDto categoryDto = new CategoryDto();
                categoryDto.setName(categories[j]);
                categoryDto.setRssLink(links[j]);
                categoryDto.setPaperId(paper_id);
                insertCategory(categoryDto);
            }
        }
    }

    /**
     * PaperDto
     */

    public int insertPaper(PaperDto paperDto) {
        int _id = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PAPER.COLUMN_NAME, paperDto.getName());
            values.put(PAPER.COLUMN_ICON, paperDto.getIcon());
            values.put(PAPER.COLUMN_CHOOSE, paperDto.getChoose());
            values.put(PAPER.COLUMN_ACTIVE, paperDto.getActive());
            values.put(PAPER.COLUMN_DATE_FORMAT, paperDto.getDateFormat());
            _id = (int) db.insert(PAPER.TABLE_NAME, null, values);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return _id;
    }

    public int countPaper() {
        int count = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select count(*) as cpaper from " + PAPER.TABLE_NAME;
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                count = (int) c.getLong(c.getColumnIndex("cpaper"));
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return count;
    }

    public void updatePatientsActive(String[] papers) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sqlWhere = "";
            db.execSQL("UPDATE " + PAPER.TABLE_NAME + " SET Active = 0");
            for (int i = 0; i < papers.length; i++) {
                if (sqlWhere == "")
                    sqlWhere += PAPER.COLUMN_NAME + " = '" + papers[i] + "'";
                else
                    sqlWhere += (" or " + PAPER.COLUMN_NAME + " = '" + papers[i] + "'");
            }
            String sql = "UPDATE " + PAPER.TABLE_NAME + " SET Active = 1 WHERE " + sqlWhere;
            db.execSQL(sql);
            db.execSQL("UPDATE " + PAPER.TABLE_NAME + " SET Choose = 0");
            db.execSQL("UPDATE " + PAPER.TABLE_NAME + " SET Choose = 1 WHERE Name = '" + Constant.DEFAULT_PAPERS[0] + "'");
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
    }

    public ArrayList<PaperDto> getPapersActive() {
        ArrayList<PaperDto> list = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from " + PAPER.TABLE_NAME + " where Active = 1 ORDER BY Name ASC";
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                do {
                    PaperDto paperDto = new PaperDto();
                    paperDto.setId((int) c.getLong(c.getColumnIndexOrThrow(PAPER._ID)));
                    paperDto.setName(c.getString(c.getColumnIndex(PAPER.COLUMN_NAME)));
                    paperDto.setChoose(c.getInt(c.getColumnIndex(PAPER.COLUMN_CHOOSE)));
                    paperDto.setActive(c.getInt(c.getColumnIndex(PAPER.COLUMN_ACTIVE)));
                    paperDto.setDateFormat(c.getString(c.getColumnIndex(PAPER.COLUMN_DATE_FORMAT)));
                    paperDto.setIcon(c.getInt(c.getColumnIndex(PAPER.COLUMN_ICON)));
                    paperDto.setCategories(getCategoriesByPaperId(paperDto.getId()));
                    list.add(paperDto);
                }
                while (c.moveToNext());
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return list;
    }

    public ArrayList<PaperDto> getAllSourcePapers() {
        ArrayList<PaperDto> list = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from " + PAPER.TABLE_NAME + " where Active = 1 ORDER BY Name ASC";
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                do {
                    PaperDto paperDto = new PaperDto();
                    paperDto.setId((int) c.getLong(c.getColumnIndexOrThrow(PAPER._ID)));
                    paperDto.setName(c.getString(c.getColumnIndex(PAPER.COLUMN_NAME)));
                    paperDto.setChoose(c.getInt(c.getColumnIndex(PAPER.COLUMN_CHOOSE)));
                    paperDto.setActive(c.getInt(c.getColumnIndex(PAPER.COLUMN_ACTIVE)));
                    paperDto.setDateFormat(c.getString(c.getColumnIndex(PAPER.COLUMN_DATE_FORMAT)));
                    paperDto.setIcon(c.getInt(c.getColumnIndex(PAPER.COLUMN_ICON)));
                    paperDto.setCategories(getCategoriesByPaperId(paperDto.getId()));
                    list.add(paperDto);
                }
                while (c.moveToNext());
            }
            db.close();
            db = this.getReadableDatabase();
            sql = "select * from " + PAPER.TABLE_NAME + " where Active = 0 ORDER BY Name ASC";
            Log.i(LOG, sql);
            c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                do {
                    PaperDto paperDto = new PaperDto();
                    paperDto.setId((int) c.getLong(c.getColumnIndexOrThrow(PAPER._ID)));
                    paperDto.setName(c.getString(c.getColumnIndex(PAPER.COLUMN_NAME)));
                    paperDto.setChoose(c.getInt(c.getColumnIndex(PAPER.COLUMN_CHOOSE)));
                    paperDto.setActive(c.getInt(c.getColumnIndex(PAPER.COLUMN_ACTIVE)));
                    paperDto.setDateFormat(c.getString(c.getColumnIndex(PAPER.COLUMN_DATE_FORMAT)));
                    paperDto.setIcon(c.getInt(c.getColumnIndex(PAPER.COLUMN_ICON)));
                    paperDto.setCategories(getCategoriesByPaperId(paperDto.getId()));
                    list.add(paperDto);
                }
                while (c.moveToNext());
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return list;
    }

    /**
     * @param categoryDto
     * @return
     */
    public int insertCategory(CategoryDto categoryDto) {
        int _id = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CATEGORY.COLUMN_NAME, categoryDto.getName());
            values.put(CATEGORY.COLUMN_RSS_LINK, categoryDto.getRssLink());
            values.put(CATEGORY.COLUMN_PAPER_ID, categoryDto.getPaperId());
            _id = (int) db.insert(CATEGORY.TABLE_NAME, null, values);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return _id;
    }

    public ArrayList<CategoryDto> getCategoriesByPaperId(int paper_id) {
        ArrayList<CategoryDto> list = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from " + CATEGORY.TABLE_NAME + " where PaperId = " + paper_id;
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                do {
                    CategoryDto categoryDto = new CategoryDto();
                    categoryDto.setId((int) c.getLong(c.getColumnIndexOrThrow(CATEGORY._ID)));
                    categoryDto.setName(c.getString(c.getColumnIndex(CATEGORY.COLUMN_NAME)));
                    categoryDto.setPaperId(c.getInt(c.getColumnIndex(CATEGORY.COLUMN_PAPER_ID)));
                    categoryDto.setRssLink(c.getString(c.getColumnIndex(CATEGORY.COLUMN_RSS_LINK)));
                    list.add(categoryDto);
                }
                while (c.moveToNext());
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return list;
    }

    /**
     * @param newsDto
     * @return
     */
    public int insertNews(NewsDto newsDto) {
        int _id = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NEWS.COLUMN_TITLE, newsDto.getTitle());
            values.put(NEWS.COLUMN_SHORT_NEWS, newsDto.getShortNews());
            values.put(NEWS.COLUMN_POSTED, newsDto.getPostedDate());
            values.put(NEWS.COLUMN_VIEWED, newsDto.getViewedDate());
            values.put(NEWS.COLUMN_IMAGE, newsDto.getImage());
            values.put(NEWS.COLUMN_IMAGE_LINK, newsDto.getImageLink());
            values.put(NEWS.COLUMN_LINK, newsDto.getLink());
            values.put(NEWS.COLUMN_CATE_ID, newsDto.getCateId());
            values.put(NEWS.COLUMN_CONTENT_HTML, newsDto.getContentHtml());
            _id = (int) db.insert(NEWS.TABLE_NAME, null, values);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return _id;
    }

    public void updatePatientChoose(PaperDto paperDto) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("UPDATE " + PAPER.TABLE_NAME + " SET Choose = 0");
            db.execSQL("UPDATE " + PAPER.TABLE_NAME + " SET Choose = " + paperDto.getChoose() + " WHERE " + PAPER._ID + " = " + paperDto.getId() + "");
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
    }

    public void updatePatientActive(ArrayList<PaperDto> paperDtos) {
        try {
            if (paperDtos.size() == 0)
                updatePatientsActive(Constant.DEFAULT_PAPERS);
            else {
                SQLiteDatabase db = this.getWritableDatabase();
                String sqlWhere = "";
                db.execSQL("UPDATE " + PAPER.TABLE_NAME + " SET Active = 0");
                for (int i = 0; i < paperDtos.size(); i++) {
                    if (sqlWhere == "")
                        sqlWhere += PAPER._ID + " = " + paperDtos.get(i).getId();
                    else
                        sqlWhere += (" or " + PAPER._ID + " = " + paperDtos.get(i).getId());
                }
                String sql = "UPDATE " + PAPER.TABLE_NAME + " SET Active = 1 WHERE " + sqlWhere;
                Log.i(LOG, sql);
                db.execSQL(sql);
                db.close();
            }
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
    }

    public int insertVariable(VariableDto variableDto) {
        int _id = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VARIABLES.COLUMN_NAME, variableDto.getName());
            values.put(VARIABLES.COLUMN_VALUE, variableDto.getValue());
            _id = (int) db.insert(VARIABLES.TABLE_NAME, null, values);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return _id;
    }

    public String getVariableByName(String name) {
        String _value = "";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM " + VARIABLES.TABLE_NAME + " WHERE " + VARIABLES.COLUMN_NAME + " = '" + name + "'";
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst())
                _value = c.getString(c.getColumnIndex(VARIABLES.COLUMN_VALUE));
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return _value;
    }

    public void updateVariable(VariableDto variableDto) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "UPDATE " + VARIABLES.TABLE_NAME + " SET Value = " + variableDto.getValue() + " WHERE Name = '" + variableDto.getName() + "'";
            db.execSQL(sql);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
    }

    public int insertSaveNews(NewsDto newsDto) {
        int _id = 0;
        try {
            int _newsId = insertNews(newsDto);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SAVE_NEWS.COLUMN_CREATED_TIME, new Date().getTime());
            values.put(SAVE_NEWS.COLUMN_NEWS_ID, _newsId);
            _id = (int) db.insert(SAVE_NEWS.TABLE_NAME, null, values);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return _id;
    }

    public boolean existsSaveNews(NewsDto newsDto) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from SaveNews s, News n WHERE s.NewsId = n._id and n.Link = '" + newsDto.getLink() + "'";
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst())
                return true;
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return false;
    }

    public ArrayList<SaveNewsDto> getSaveNewsList() {
        ArrayList<SaveNewsDto> list = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from SaveNews s, News n WHERE s.NewsId = n._id ORDER BY s.CreatedTime DESC";
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                do {
                    NewsDto newsDto = new NewsDto();
                    newsDto.setId((int) c.getLong(c.getColumnIndexOrThrow(NEWS._ID)));
                    newsDto.setCateId(c.getInt(c.getColumnIndex(NEWS.COLUMN_CATE_ID)));

                    newsDto.setTitle(c.getString(c.getColumnIndex(NEWS.COLUMN_TITLE)));
                    newsDto.setShortNews(c.getString(c.getColumnIndex(NEWS.COLUMN_SHORT_NEWS)));

                    newsDto.setLink(c.getString(c.getColumnIndex(NEWS.COLUMN_LINK)));
                    newsDto.setImage(c.getBlob(c.getColumnIndex(NEWS.COLUMN_IMAGE)));
                    newsDto.setImageLink(c.getString(c.getColumnIndex(NEWS.COLUMN_IMAGE_LINK)));

                    newsDto.setPostedDate(c.getLong(c.getColumnIndex(NEWS.COLUMN_POSTED)));
                    newsDto.setViewedDate(c.getLong(c.getColumnIndex(NEWS.COLUMN_VIEWED)));

                    newsDto.setContentHtml(c.getString(c.getColumnIndex(NEWS.COLUMN_CONTENT_HTML)));

                    SaveNewsDto saveNewsDto = new SaveNewsDto();
                    saveNewsDto.setNewsDto(newsDto);
                    saveNewsDto.setCreatedTime(c.getLong(c.getColumnIndex(SAVE_NEWS.COLUMN_CREATED_TIME)));
                    saveNewsDto.setNewsId(newsDto.getId());
                    list.add(saveNewsDto);
                }
                while (c.moveToNext());
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return list;
    }

    public int deleteSaveNews(SaveNewsDto saveNewsDto) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + SAVE_NEWS.TABLE_NAME + " WHERE " + SAVE_NEWS.COLUMN_NEWS_ID + " = " + saveNewsDto.getNewsId());
            db.close();
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int countSaveNews() {
        int count = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select count(*) as num from " + SAVE_NEWS.TABLE_NAME;
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                count = (int) c.getLong(c.getColumnIndex("num"));
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return count;
    }

    public int deleteAllSaveNews() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + SAVE_NEWS.TABLE_NAME);
            db.close();
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public NewsDto getNewsByLink(String link) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from News WHERE Link = '" + link + "'";
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                NewsDto newsDto = new NewsDto();
                newsDto.setId((int) c.getLong(c.getColumnIndexOrThrow(NEWS._ID)));
                newsDto.setCateId(c.getInt(c.getColumnIndex(NEWS.COLUMN_CATE_ID)));

                newsDto.setTitle(c.getString(c.getColumnIndex(NEWS.COLUMN_TITLE)));
                newsDto.setShortNews(c.getString(c.getColumnIndex(NEWS.COLUMN_SHORT_NEWS)));
                newsDto.setContentHtml(c.getString(c.getColumnIndex(NEWS.COLUMN_CONTENT_HTML)));

                newsDto.setLink(c.getString(c.getColumnIndex(NEWS.COLUMN_LINK)));
                newsDto.setImage(c.getBlob(c.getColumnIndex(NEWS.COLUMN_IMAGE)));
                newsDto.setImageLink(c.getString(c.getColumnIndex(NEWS.COLUMN_IMAGE_LINK)));

                newsDto.setPostedDate(c.getLong(c.getColumnIndex(NEWS.COLUMN_POSTED)));
                newsDto.setViewedDate(c.getLong(c.getColumnIndex(NEWS.COLUMN_VIEWED)));
                db.close();
                return newsDto;
            } else
                return null;
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
            return null;
        }
    }

    public void updateNewsContent(NewsDto newsDto) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "UPDATE " + NEWS.TABLE_NAME + " SET ContentHtml = '"
                    + newsDto.getContentHtml() + "' WHERE Link = '"
                    + newsDto.getLink() + "'";
            Log.i(LOG, sql);
            db.execSQL(sql);
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
    }

    public ArrayList<NewsDto> getNewsList(int LIMIT, int OFFSET) {
        ArrayList<NewsDto> result = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM News LIMIT " + LIMIT + " OFFSET " + OFFSET;
            Log.i(LOG, sql);
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                do {
                    NewsDto newsDto = new NewsDto();
                    newsDto.setId((int) c.getLong(c.getColumnIndexOrThrow(NEWS._ID)));
                    newsDto.setCateId(c.getInt(c.getColumnIndex(NEWS.COLUMN_CATE_ID)));

                    newsDto.setTitle(c.getString(c.getColumnIndex(NEWS.COLUMN_TITLE)));
                    newsDto.setShortNews(c.getString(c.getColumnIndex(NEWS.COLUMN_SHORT_NEWS)));
                    newsDto.setContentHtml(c.getString(c.getColumnIndex(NEWS.COLUMN_CONTENT_HTML)));

                    newsDto.setLink(c.getString(c.getColumnIndex(NEWS.COLUMN_LINK)));
                    newsDto.setImage(c.getBlob(c.getColumnIndex(NEWS.COLUMN_IMAGE)));
                    newsDto.setImageLink(c.getString(c.getColumnIndex(NEWS.COLUMN_IMAGE_LINK)));

                    newsDto.setPostedDate(c.getLong(c.getColumnIndex(NEWS.COLUMN_POSTED)));
                    newsDto.setViewedDate(c.getLong(c.getColumnIndex(NEWS.COLUMN_VIEWED)));
                    result.add(newsDto);
                }
                while (c.moveToNext());
            }
            db.close();
        } catch (Exception ex) {
            Log.i(LOG, ex.toString());
        }
        return result;
    }
}
