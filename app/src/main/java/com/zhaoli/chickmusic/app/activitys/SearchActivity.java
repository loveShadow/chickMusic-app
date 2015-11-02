package com.zhaoli.chickmusic.app.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.adapters.IOnItemClickListener;
import com.zhaoli.chickmusic.app.adapters.RecyclerItemClickListener;
import com.zhaoli.chickmusic.app.adapters.SearchResultAdapter;
import com.zhaoli.chickmusic.app.animation.ParabolaAnimationUtil;
import com.zhaoli.chickmusic.app.eventbus.event.AddSongToListEvent;
import com.zhaoli.chickmusic.app.recyclerview.layoutmanager.ABaseLinearLayoutManager;
import com.zhaoli.chickmusic.app.recyclerview.listener.IOnRecyclerViewScrollLocationListener;
import com.zhaoli.chickmusic.data.Const;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.data.rankdata.WangyiSearchSongResultJson;
import com.zhaoli.chickmusic.data.rankdata.WangyiSongJson;
import com.zhaoli.chickmusic.request.SearchSongRequest;
import com.zhaoli.chickmusic.utils.AppTools;
import com.zhaoli.chickmusic.utils.DensityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoli on 2015/10/17.
 */
public class SearchActivity extends Activity implements View.OnClickListener{

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        context.startActivity(intent);
    }

    private Button returnButton = null;
    private Button searchButton = null;
    private Button clearButton = null;
    private EditText searchEditText = null;
    private RecyclerView recyclerView = null;
    private RelativeLayout loadingBar = null;

    private RelativeLayout headerLayout = null;
    private RelativeLayout footerLayout = null;
    private TextView searchSongCountTextView = null;
    private TextView hasMoreTextView = null;

    private ImageView addSongAnimImageView = null;
    private LinearLayout addSongAnimLayout = null;

    private RelativeLayout rootView = null;
    private SearchResultAdapter searchResultAdapter = null;
    private ABaseLinearLayoutManager linearLayoutManager = null;

    private String currentSearchKey = "";       //当前搜索关键字
    private String lastSearchKey = "";          //上一次搜索关键字
    private int searchSongCount = 0;
    private boolean isLoadMoreSong = false;

    private Response.Listener<String> searchSuccessListener = null;
    private Response.ErrorListener searchErrorListener = null;

    private final String REQUEST_SEARCH_SONG = "RequestSearchSong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CMApplication.getInstance().getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CMApplication.getInstance().getEventBus().unregister(this);
    }

    void initView() {
        rootView = (RelativeLayout)findViewById(R.id.rootView);

        returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);
        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        clearButton = (Button)findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);
        loadingBar = (RelativeLayout)findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.GONE);
        searchEditText = (EditText)findViewById(R.id.searchEdit);
        searchEditText.addTextChangedListener(new EditChangedListener());

        headerLayout = (RelativeLayout)findViewById(R.id.headerLayout);
        headerLayout.setVisibility(View.GONE);
        footerLayout = (RelativeLayout)findViewById(R.id.footerLayout);
        footerLayout.setVisibility(View.GONE);
        searchSongCountTextView = (TextView)findViewById(R.id.searchSongCountText);
        hasMoreTextView = (TextView)findViewById(R.id.hasMoreText);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager = new ABaseLinearLayoutManager(this);
        linearLayoutManager.setOnRecyclerViewScrollLocationListener(recyclerView, getOnRecyclerViewScrollLocationListener());
        recyclerView.setLayoutManager(linearLayoutManager);
        searchResultAdapter = new SearchResultAdapter(this, getRankSongItemListener());
        recyclerView.setAdapter(searchResultAdapter);

        searchSuccessListener = getSearchSuccessListener();
        searchErrorListener = getSearchErrorListener();

        addSongAnimImageView = new ImageView(this);
        addSongAnimImageView.setBackgroundResource(R.mipmap.add_animation_icon);
        addSongAnimImageView.setVisibility(View.GONE);
        initAddSongAnimLayout();
    }

    private IOnItemClickListener getRankSongItemListener() {
        return new IOnItemClickListener() {
            @Override
            public void onClicked(View view, Song song) {
                //播放歌曲
                int[] startPos = new int[2];
                view.getLocationOnScreen(startPos);
                addSongToPlayerList(song, startPos, true);
            }

            @Override
            public void onLongClicked(View view, Song song) {

            }
        };
    }

    private void initAddSongAnimLayout() {
        addSongAnimLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addSongAnimLayout.setLayoutParams(lp);
        addSongAnimLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(addSongAnimLayout);
        addSongAnimLayout.addView(addSongAnimImageView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (needHideInput(view, ev)) {
                searchEditText.clearFocus();
                AppTools.hideInputSoft(this, searchEditText);
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean needHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left &&
                    event.getX() < right &&
                    event.getY() > top &&
                    event.getY() < bottom) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == returnButton) {
            SearchActivity.this.finish();
        } else if (view == clearButton) {
            searchEditText.setText("");
        } else if (view == searchButton) {
            //搜索
            headerLayout.setVisibility(View.GONE);
            isLoadMoreSong = false;
            startLoading();
            searchSong(searchEditText.getText().toString());
        }
    }

    private class EditChangedListener implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (! editable.toString().isEmpty()) {
                clearButton.setVisibility(View.VISIBLE);
            } else {
                clearButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void startLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        loadingBar.setVisibility(View.GONE);
    }

    private void searchSong(String key) {
        lastSearchKey = currentSearchKey;
        currentSearchKey = key;
        CMApplication.getInstance().cancelPendingRequests(REQUEST_SEARCH_SONG);
        String url = Const.SEARCH_SONG_URL;
        Map<String, String> par = new HashMap<>();
        par.put("src", "lofter");
        par.put("type", "1");
        par.put("filterDj", "true");
        par.put("s", key.replaceAll(" ", ""));
        par.put("limit", "10");
        par.put("offset", String.valueOf(searchResultAdapter.getItemCount()));
        par.put("callback", "");
        SearchSongRequest stringRequest = new SearchSongRequest(Request.Method.POST, url, searchSuccessListener, searchErrorListener, par);
        CMApplication.getInstance().addToRequestQueue(stringRequest, REQUEST_SEARCH_SONG);
    }

    private Response.Listener<String> getSearchSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                footerLayout.setVisibility(View.GONE);
                stopLoading();
                onSearchSuccess(response);
            }
        };
    }

    private Response.ErrorListener getSearchErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopLoading();
                onSearchError();
            }
        };
    }

    private IOnRecyclerViewScrollLocationListener getOnRecyclerViewScrollLocationListener() {
        return new IOnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {
            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {
                //加载更多数据
                if (searchSongCount > searchResultAdapter.getItemCount()) {
                    hasMoreTextView.setText("正在加载...");
                    footerLayout.setVisibility(View.VISIBLE);
                    isLoadMoreSong = true;
                    searchSong(currentSearchKey);
                }
            }
        };
    }

    private void onSearchSuccess(String response) {
        Gson gson = new Gson();
        WangyiSearchSongResultJson searchSongJson = gson.fromJson(response, WangyiSearchSongResultJson.class);
        if (searchSongJson != null && searchSongJson.isOK()) {
            WangyiSearchSongResultJson.WangyiSearchSongJson result = searchSongJson.getResult();
            if (result != null) {
                searchSongCount = result.getSongCount();
                headerLayout.setVisibility(View.VISIBLE);
                searchSongCountTextView.setText("共搜索到 " + searchSongCount + " 条数据");
                List<WangyiSongJson> songJsons = result.getSongs();
                List<Song> songList = new ArrayList<>();
                for (int i = 0; i < songJsons.size(); i++) {
                    Song song = new Song(songJsons.get(i));
                    songList.add(song);
                }
                int currentIndex = linearLayoutManager.findLastVisibleItemPosition();
                if (isLoadMoreSong) {
                    searchResultAdapter.addData(songList);
                    searchResultAdapter.notifyItemChanged(currentIndex);
                } else {
                    searchResultAdapter.setData(songList);
                    searchResultAdapter.notifyDataSetChanged();
                }
                return;
            }
        }
        onSearchError();
        //更换成上一次搜索的更多加载
        if (searchResultAdapter.getItemCount() > 0) {
            isLoadMoreSong = true;
            currentSearchKey = lastSearchKey;
        }
    }

    private void onSearchError() {
        AppTools.showToast("搜索失败，请重试", this);
    }

    /** Event Bus 处理 */
    public void onEventMainThread(AddSongToListEvent event) {
        //获取动画结束位置
        addSongToPlayerList(event.getSong(), event.getStartPos(), false);
    }

    /**
     *
     * @param toPlay 是否立马播放
     */
    private void addSongToPlayerList(Song song, int[] startPos, boolean toPlay) {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int[] endPos = new int[]{ point.x / 2, point.y};
        ParabolaAnimationUtil.startAnimation(addSongAnimImageView, startPos, endPos);
        AppTools.showToast("已添加至播放列表", this);

        PlayerManager.getmInstance().addSongToList(song, toPlay);
    }
}
