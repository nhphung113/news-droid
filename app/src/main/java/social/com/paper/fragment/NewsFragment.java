package social.com.paper.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import social.com.paper.R;
import social.com.paper.activity.NewsDetailsActivity;
import social.com.paper.adapter.NewsListAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.google_utils.ImageFetcher;
import social.com.paper.google_utils.Utils;
import social.com.paper.model.NewsItem;
import social.com.paper.utils.HelperUtils;
import social.com.paper.google_utils.ImageCache;
import social.com.paper.utils.RssParser;
import social.com.paper.utils.Variables;

import android.support.v4.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

/**
 * Created by phung nguyen on 7/22/2015.
 */
public class NewsFragment extends Fragment {
    int PAGE_NUMBER = 4;

    SwipeMenuListView mListView;
    View mRootView;
    NewsListAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ArrayList<NewsItem> newsItemLst = new ArrayList<>();
    public static ArrayList<NewsDto> newsDtoLst = new ArrayList<>();
    PaperDto paperDto;
    int positionCate = 0;
    ProgressDialog mDialog;
    ProgressDialog mDialogSaveNews;

    String link = "";
    int posContextMenu = -1;

    // cache image.
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    public static ImageFetcher mImageFetcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_news_fragment, container, false);
        initialize();
        eventControls();
        registerForContextMenu(mListView);
        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(null);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (posContextMenu != 1)
            menu.setHeaderTitle(newsItemLst.get(posContextMenu).getTitle());
        else
            menu.setHeaderTitle(paperDto.getName());

        menu.add(0, v.getId(), 0, getResources().getString(R.string.menu_context_save_news)); //groupId, itemId, order, title
        menu.add(0, v.getId(), 0, getResources().getString(R.string.menu_context_share));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == getResources().getString(R.string.menu_context_save_news)) {
            saveNews(newsDtoLst.get(posContextMenu));
        } else if (item.getTitle() == getResources().getString(R.string.menu_context_share)) {
            shareNews(newsDtoLst.get(posContextMenu));
        } else {
            return false;
        }
        return true;
    }

    private void shareNews(NewsDto newsDto) {
        if (newsDto != null) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, newsDto.getTitle());
            share.putExtra(Intent.EXTRA_TEXT, newsDto.getLink());

            startActivity(Intent.createChooser(share, getResources().getString(R.string.toast_share_news_link)));
        } else
            Toast.makeText(getActivity(), R.string.toast_dont_share_news_link, Toast.LENGTH_SHORT).show();
    }

    private void saveNews(NewsDto newsDto) {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        if (!db.existsSaveNews(newsDto)) {
            if (HelperUtils.isConnectingToInternet(getActivity())) {
                mDialogSaveNews = new ProgressDialog(getActivity());
                mDialogSaveNews.setMessage(getResources().getString(R.string.menu_context_saving_news));
                mDialogSaveNews.show();
                new LoadNewsTask().execute(newsDto);
            } else
                Toast.makeText(getActivity(), R.string.toast_you_need_connent_internet, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), R.string.toast_you_saved_this_news, Toast.LENGTH_SHORT).show();
    }

    private void eventControls() {
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        saveNews(newsDtoLst.get(position));
                        break;
                    case 1:
                        shareNews(newsDtoLst.get(position));
                        break;
                }
                return false;
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posContextMenu = position;
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsDto newsDto = newsDtoLst.get(position);
                NewsItem newsItem = newsItemLst.get(position);
                newsItem.setPaperName(paperDto.getName());

                Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Variables.KEY_SEND_NEWS_DTO, newsDto);
                bundle.putSerializable(Variables.KEY_SEND_NEWS_ITEM, newsItem);
                bundle.putString(Variables.KEY_SEND_PAPER_NAME, paperDto.getName());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);

                if ((firstVisibleItem + visibleItemCount) == newsItemLst.size() && newsItemLst.size() != newsDtoLst.size() && newsDtoLst.size() > 0) {
                    int count = (newsItemLst.size() + newsDtoLst.size() / PAGE_NUMBER);
                    if (count <= newsDtoLst.size()) {
                        for (int i = newsItemLst.size(); i < count; i++) {
                            try {
                                NewsDto newsDto = newsDtoLst.get(i);
                                NewsItem item = new NewsItem();
                                item.setTitle(newsDto.getTitle());
                                item.setShortNews(newsDto.getShortNews());
                                item.setImageLink(newsDto.getImageLink());
                                item.setDateTimeAgo(newsDto.getPostedDate());
                                item.setLink(newsDto.getLink());
                                item.setCategoryName(paperDto.getCategories().get(positionCate).getName());
                                newsItemLst.add(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (HelperUtils.isConnectingToInternet(getActivity())) {
                    new LoadNewsListTask().execute(positionCate);
                } else
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initialize() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        newsItemLst = new ArrayList<>();
        mAdapter = new NewsListAdapter(getActivity(), R.layout.custom_news_item_layout, newsItemLst);
        mListView = (SwipeMenuListView) mRootView.findViewById(R.id.lvNewsList);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "save" item
                SwipeMenuItem saveItem = new SwipeMenuItem(getActivity());
                saveItem.setWidth(200);
                saveItem.setIcon(R.drawable.ic_save_news);
                menu.addMenuItem(saveItem);

                // create "share" item
                SwipeMenuItem shareItem = new SwipeMenuItem(getActivity());
                shareItem.setWidth(200);
                shareItem.setIcon(R.drawable.ic_share_black_36dp);
                menu.addMenuItem(shareItem);
            }
        };

        mListView.setMenuCreator(creator);
        mListView.setAdapter(mAdapter);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        if (HelperUtils.isConnectingToInternet(getActivity())) {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                positionCate = bundle.getInt(Variables.KEY_CATEGORY);
                paperDto = (PaperDto) bundle.getSerializable(Variables.KEY_PAPER);
                link = paperDto.getCategories().get(positionCate).getRssLink();
            }

            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getResources().getString(R.string.toast_loading));
            mDialog.show();

            new LoadNewsListTask().execute(positionCate);
        } else
            Toast.makeText(getActivity(), R.string.toast_internet_failed, Toast.LENGTH_SHORT).show();
    }

    public class LoadNewsListTask extends AsyncTask<Integer, Void, ArrayList<NewsDto>> {

        @Override
        protected ArrayList<NewsDto> doInBackground(Integer... params) {
            return RssParser.getNewsList(params[0], paperDto, getActivity());
        }

        @Override
        protected void onPostExecute(ArrayList<NewsDto> list) {
            super.onPostExecute(list);
            try {
                if (list.size() == 0) {
                    Toast.makeText(getActivity(), R.string.toast_dont_news, Toast.LENGTH_SHORT).show();
                    newsItemLst = new ArrayList<>();
                    mAdapter.notifyDataSetChanged();
                } else {
                    newsDtoLst = list;
                    newsItemLst = new ArrayList<>();
                    PAGE_NUMBER = list.size() / Variables.NUMBERS_NEWS_ON_LIST;
                    for (int i = 0; i < list.size() / PAGE_NUMBER; i++) {
                        NewsDto newsDto = list.get(i);
                        NewsItem item = new NewsItem();
                        item.setTitle(newsDto.getTitle());
                        item.setShortNews(newsDto.getShortNews());
                        item.setImageLink(newsDto.getImageLink());
                        item.setDateTimeAgo(newsDto.getPostedDate());
                        item.setLink(newsDto.getLink());
                        item.setCategoryName(paperDto.getCategories().get(positionCate).getName());
                        newsItemLst.add(item);
                    }

                    try {
                        mListView = (SwipeMenuListView) mRootView.findViewById(R.id.lvNewsList);
                        mAdapter = new NewsListAdapter(getActivity(), R.layout.custom_news_item_layout, newsItemLst);
                        mListView.setAdapter(mAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDialog.isShowing())
                mDialog.dismiss();

            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class LoadNewsTask extends AsyncTask<NewsDto, Void, String> {
        NewsDto newsDto;
        Document document;

        @Override
        protected String doInBackground(NewsDto... params) {
            try {
                newsDto = params[0];
                document = Jsoup.connect(newsDto.getLink()).get();
                return HelperUtils.getPaperContentHtml(document, paperDto.getName());
            } catch (IOException e) {
                e.printStackTrace();
                document = null;
                return "";
            }
        }

        @Override
        protected void onPostExecute(String html) {
            super.onPostExecute(html);
            String message = "";
            if (document != null) {
                if (html == "")
                    html = document.html();
                else {
                    html = html.replace("href", "hrefs");
                    html = "<html><head><style type='text/css'>body{text-align:justify;} img{width:100%25;} h1{text-align:left;} h2{text-align:left;} </style></head>"
                            + "<body>" + html + "</body></html>";
                }
                newsDto.setContentHtml(html);
                DatabaseHandler db = new DatabaseHandler(getActivity());
                if (db.insertSaveNews(newsDto) != 0)
                    message = getResources().getString(R.string.toast_saved_complete);
                else
                    message = getResources().getString(R.string.toast_saved_failed);
            } else
                message = getResources().getString(R.string.toast_saved_failed);

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            if (mDialogSaveNews.isShowing())
                mDialogSaveNews.dismiss();
        }
    }

}