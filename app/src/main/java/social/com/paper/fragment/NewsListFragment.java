package social.com.paper.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import social.com.paper.R;
import social.com.paper.activity.DetailsActivity;
import social.com.paper.activity.MainActivity;
import social.com.paper.adapter.NewsAdapter;
import social.com.paper.database.DatabaseHandler;
import social.com.paper.dto.NewsDto;
import social.com.paper.dto.PaperDto;
import social.com.paper.ebus.HomeEvent;
import social.com.paper.model.NewsItem;
import social.com.paper.utils.Constant;
import social.com.paper.utils.HelpUtils;
import social.com.paper.utils.RssParser;

/**
 * Created by phung nguyen on 7/22/2015.
 */
public class NewsListFragment extends Fragment {
    private static final String TAG = NewsListFragment.class.getName();
    private int PAGE_NUMBER = 4;

    @Bind(R.id.lvNewsList)
    ListView mListView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NewsAdapter mAdapter;
    private ArrayList<NewsItem> newsItemLst = new ArrayList<>();
    public static ArrayList<NewsDto> newsDtoLst = new ArrayList<>();
    private PaperDto paperDto;
    private int positionCate = 0;
    private ProgressDialog mDialog;
    private ProgressDialog mDialogSaveNews;

    private String link = "";
    private int posContextMenu = -1;
    private Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_news_fragment, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        initialize();
        initEvents();
        registerForContextMenu(mListView);

        return view;
    }

    @Subscribe
    public void onHomeEvent(HomeEvent event) {
        Log.i(TAG, "onHomeEvent");
        positionCate = event.positionCate;
        paperDto = event.paperDto;
        if (paperDto != null) {
            link = paperDto.getCategories().get(positionCate).getRssLink();
            if (!TextUtils.isEmpty(link)) {
                mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage(getResources().getString(R.string.toast_loading));
                mDialog.setCancelable(false);
                mDialog.show();
                new LoadNewsListTask().execute(positionCate);

                Observable<ArrayList<NewsDto>> observable = Observable.fromCallable(new Callable<ArrayList<NewsDto>>() {
                    @Override
                    public ArrayList<NewsDto> call() throws Exception {
                        return RssParser.getNewsList(positionCate, paperDto, getActivity());
                    }
                });
                subscription = observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ArrayList<NewsDto>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.toString());
                            }

                            @Override
                            public void onNext(ArrayList<NewsDto> list) {
                                loadUI(list);
                            }
                        });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
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
            if (HelpUtils.isConnectingToInternet(getActivity())) {
                mDialogSaveNews = new ProgressDialog(getActivity());
                mDialogSaveNews.setMessage(getResources().getString(R.string.menu_context_saving_news));
                mDialogSaveNews.setCancelable(false);
                mDialogSaveNews.show();
                new LoadNewsTask().execute(newsDto);
            } else
                Toast.makeText(getActivity(), R.string.toast_you_need_connent_internet, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), R.string.toast_you_saved_this_news, Toast.LENGTH_SHORT).show();
    }

    private void initEvents() {
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

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_SEND_NEWS_DTO, newsDto);
                bundle.putSerializable(Constant.KEY_SEND_NEWS_ITEM, newsItem);
                bundle.putString(Constant.KEY_SEND_PAPER_NAME, paperDto.getName());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
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
                if (HelpUtils.isConnectingToInternet(getActivity())) {
                    new LoadNewsListTask().execute(positionCate);
                } else
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initialize() {
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mAdapter = new NewsAdapter(getActivity(), newsItemLst);
        mListView.setAdapter(mAdapter);

        if (HelpUtils.isConnectingToInternet(getActivity())) {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                positionCate = bundle.getInt(Constant.KEY_CATEGORY);
                paperDto = (PaperDto) bundle.getSerializable(Constant.KEY_PAPER);
                if (paperDto != null) {
                    link = paperDto.getCategories().get(positionCate).getRssLink();
                    if (!TextUtils.isEmpty(link)) {
                        mDialog = new ProgressDialog(getActivity());
                        mDialog.setMessage(getResources().getString(R.string.toast_loading));
                        mDialog.setCancelable(false);
                        mDialog.show();
                        new LoadNewsListTask().execute(positionCate);
                    }
                }
            }
        } else {
            Toast.makeText(getActivity(), R.string.toast_internet_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public static NewsListFragment newInstance(int position, PaperDto mPaperCurrent) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CATEGORY, position);
        bundle.putSerializable(Constant.KEY_PAPER, mPaperCurrent);
        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public class LoadNewsListTask extends AsyncTask<Integer, Void, ArrayList<NewsDto>> {

        @Override
        protected ArrayList<NewsDto> doInBackground(Integer... params) {
            return RssParser.getNewsList(params[0], paperDto, getActivity());
        }

        @Override
        protected void onPostExecute(ArrayList<NewsDto> result) {
            super.onPostExecute(result);
            loadUI(result);
        }
    }

    private void loadUI(ArrayList<NewsDto> result) {
        try {
            newsItemLst.clear();
            MainActivity.newsCurrentLst = result;
            if (result.size() == 0) {
                Toast.makeText(getActivity(), R.string.toast_dont_news, Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
            } else {
                newsDtoLst = result;
                PAGE_NUMBER = result.size() / Constant.NUMBERS_NEWS_ON_LIST;
                for (int i = 0; i < result.size() / PAGE_NUMBER; i++) {
                    NewsDto newsDto = result.get(i);
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
                    mAdapter = new NewsAdapter(getActivity(), newsItemLst);
                    mListView.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
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
                return HelpUtils.getPaperContentHtml(document, paperDto.getName());
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