package com.zhaoli.chickmusic.app.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.adapters.IOnItemClickListener;
import com.zhaoli.chickmusic.app.adapters.RankTypeAdapter;
import com.zhaoli.chickmusic.app.adapters.RecyclerItemClickListener;
import com.zhaoli.chickmusic.app.adapters.RankTypeLayoutManager;
import com.zhaoli.chickmusic.app.adapters.SearchResultAdapter;
import com.zhaoli.chickmusic.app.animation.ParabolaAnimationUtil;
import com.zhaoli.chickmusic.app.eventbus.event.AddSongToListEvent;
import com.zhaoli.chickmusic.app.recyclerview.layoutmanager.ABaseLinearLayoutManager;
import com.zhaoli.chickmusic.app.recyclerview.listener.IOnRecyclerViewScrollLocationListener;
import com.zhaoli.chickmusic.data.Const;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.data.rankdata.BaiduRankSongJson;
import com.zhaoli.chickmusic.request.GetSongInfoRequest;
import com.zhaoli.chickmusic.utils.AppTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/18.
 */
public class RankActivity extends Activity implements View.OnClickListener{
    public static final String RANK_TYPE = "RankType";

    public static void startActivity(Context context, int rankType) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(RANK_TYPE, rankType);
        intent.putExtras(bundle);
        intent.setClass(context, RankActivity.class);
        context.startActivity(intent);
    }

    private RecyclerView rankTypeViewList = null;
    private RankTypeAdapter rankTypeAdapter = null;
    private RecyclerView rankSongListView = null;
    private SearchResultAdapter rankSongAdapter = null;
    private ABaseLinearLayoutManager linearLayoutManager = null;
    private TextView rankTypeTextView = null;
    private Button returnButton = null;
    private TextView titleTextView = null;

    private ImageView addSongAnimImageView = null;
    private LinearLayout addSongAnimLayout = null;

    private RelativeLayout rootView = null;

    private String[] rankTexts = null;

    private int currentRankId = -1;

    private Response.Listener<String> randomSongSuccessListener = null;
    private Response.ErrorListener randomSongErrorListener = null;

    private RecyclerItemClickListener rankTypeItemListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_activity_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentRankId = bundle.getInt(RANK_TYPE, -1);
        }

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

    private void initView() {
        rootView = (RelativeLayout)findViewById(R.id.rootView);

        titleTextView = (TextView)findViewById(R.id.titleTextView);
        titleTextView.setText("榜单歌曲");

        returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);

        rankTypeTextView = (TextView)findViewById(R.id.rankTypeTextView);
        rankTypeViewList = (RecyclerView)findViewById(R.id.rankTypeList);
        rankSongListView = (RecyclerView)findViewById(R.id.rankTypeSongResult);

        rankTexts = getResources().getStringArray(R.array.rankTypeText);

        rankTypeItemListener = getRankTypeItemListener();
        initRankTypeList();
        initRankSongList();

        updateRankTypeTextView();
        randomSongSuccessListener = getRandomSongSuccessListener();
        randomSongErrorListener = getRandomSongErrorListener();
        requestRankSong(currentRankId);

        addSongAnimImageView = new ImageView(this);
        addSongAnimImageView.setBackgroundResource(R.mipmap.add_animation_icon);
        addSongAnimImageView.setVisibility(View.GONE);
        initAddSongAnimLayout();
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

    private void initRankTypeList() {
        rankTypeAdapter = new RankTypeAdapter(this, rankTexts, Const.RANK_TYPE_LIST);
        rankTypeViewList.setAdapter(rankTypeAdapter);
        rankTypeViewList.setLayoutManager(new RankTypeLayoutManager(this, 3, rankTypeViewList));
        rankTypeViewList.addOnItemTouchListener(rankTypeItemListener);
    }

    private void initRankSongList() {
        linearLayoutManager = new ABaseLinearLayoutManager(this);
        linearLayoutManager.setOnRecyclerViewScrollLocationListener(rankSongListView, getOnRecyclerViewScrollLocationListener());
        rankSongListView.setLayoutManager(linearLayoutManager);
        rankSongAdapter = new SearchResultAdapter(this, getRankSongItemListener());
        rankSongListView.setAdapter(rankSongAdapter);
    }

    private void updateRankTypeTextView() {
        for (int i = 0; i < Const.RANK_TYPE_LIST.length; i ++) {
            if (Const.RANK_TYPE_LIST[i] == currentRankId) {
                rankTypeTextView.setText(rankTexts[i]);
                break;
            }
        }
    }

    private void requestRankSong(int rankID) {
        String url = Const.RANK_LIST_SONG_URL + "&type=" + rankID + "&size=10" + "&offset=" + rankSongAdapter.getItemCount();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, randomSongSuccessListener, randomSongErrorListener);
        CMApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private boolean hasMore = false;
    private boolean isHasMoreRequest = false;
    private Response.Listener<String> getRandomSongSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                BaiduRankSongJson baiduRankSongJson = gson.fromJson(s, BaiduRankSongJson.class);
                List<Song> songList = new ArrayList<>();
                for (int i = 0; i < baiduRankSongJson.getSong_list().size(); i ++) {
                    songList.add(new Song(baiduRankSongJson.getSong_list().get(i)));
                }
                hasMore = (baiduRankSongJson.getBillboard().getHavemore() == 1) ? true : false;
                currentRankId = Integer.parseInt(baiduRankSongJson.getBillboard().getBillboard_type());
                updateRankTypeTextView();

                if (isHasMoreRequest) {
                    rankSongAdapter.addData(songList);
                    rankSongAdapter.notifyItemChanged(rankSongAdapter.getItemCount());
                } else {
                    rankSongAdapter.setData(songList);
                    rankSongAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private Response.ErrorListener getRandomSongErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onGetRankError();
            }
        };
    }

    private RecyclerItemClickListener getRankTypeItemListener() {
        return new RecyclerItemClickListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                isHasMoreRequest = false;
                requestRankSong((int)view.getTag());
            }
        });
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

    private IOnRecyclerViewScrollLocationListener getOnRecyclerViewScrollLocationListener() {
        return new IOnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {

            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {
                //加载更多数据
                if (hasMore) {
                    isHasMoreRequest = true;
                    requestRankSong(currentRankId);
                }
            }
        };
    }

    private void onGetRankError() {
        AppTools.showToast("获取排行榜失败，请重试", this);
    }

    @Override
    public void onClick(View v) {
        if (v == returnButton) {
            finish();
        }
    }

    /** Event Bus 处理 */
    public void onEventMainThread(AddSongToListEvent event) {
        addSongToPlayerList(event.getSong(), event.getStartPos(), false);
    }

    private void addSongToPlayerList(Song song, int[] startPos, final boolean isPlay) {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int[] endPos = new int[]{ point.x / 2, point.y};
        ParabolaAnimationUtil.startAnimation(addSongAnimImageView, startPos, endPos);

        //请求歌曲详细信息
        new GetSongInfoRequest(song, new GetSongInfoRequest.IOnGetSongInfoListener() {
            @Override
            public void onSuccess(Song song) {
                PlayerManager.getmInstance().addSongToList(song, isPlay);
                AppTools.showToast("已添加至播放列表", RankActivity.this);
            }

            @Override
            public void onFailed(Song song) {
                AppTools.showToast("获取歌曲 " + song.getSongName() + " 信息失败~请重新添加", RankActivity.this);
            }
        });
    }
}
