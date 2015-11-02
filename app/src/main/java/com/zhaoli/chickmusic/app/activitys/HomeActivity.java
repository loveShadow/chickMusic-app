package com.zhaoli.chickmusic.app.activitys;

import android.app.Activity;
import android.app.FragmentManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.fragments.ClassifyButtonFragment;
import com.zhaoli.chickmusic.app.fragments.LocalImageViewPageFragment;
import com.zhaoli.chickmusic.app.fragments.PlayListFragment;
import com.zhaoli.chickmusic.app.fragments.PlaySongFragmnet;
import com.zhaoli.chickmusic.data.Const;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.data.rankdata.BaiduRankSongJson;
import com.zhaoli.chickmusic.request.GetSongInfoRequest;
import com.zhaoli.chickmusic.utils.AppTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by zhaoli on 2015/10/15.
 */
public class HomeActivity extends Activity implements SensorEventListener,
        View.OnClickListener,
        ClassifyButtonFragment.IOnUpdateUIListener,
        LocalImageViewPageFragment.IOnItemClickListener,
        PlaySongFragmnet.IOnPlaySongFragmentListener,
        PlayListFragment.IOnPlayListFragmentListener {

    private TextView titleTextView = null;
    private FrameLayout centeredFragment = null;
    private RelativeLayout progressBar = null;
    private Animation shakeAnimation = null;
    private TextView songInfoTextView = null;
    private Button searchButton = null;
    private FrameLayout playListLayout = null;

    private FragmentManager fragmentManager = null;
    private LocalImageViewPageFragment localImageViewPageFragment = null;
    private PlaySongFragmnet playSongFragmnet = null;
    private PlayListFragment playListFragment = null;

    private SensorManager sensorManager = null;
    private Vibrator vibrator = null;

    private Response.Listener<String> randomSongSuccessListener = null;
    private Response.ErrorListener randomSongErrorListener = null;

    private final int MAX_RANDOM_COUNT = 5;
    private List<Song> randomSongList = new ArrayList<>();

    private final int[] classifyButtonLayoutList = new int[]{
            R.layout.classify_button_layout_1,
            R.layout.classify_button_layout_2,
            R.layout.classify_button_layout_3,
            R.layout.classify_button_layout_4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initView() {
        fragmentManager = getFragmentManager();
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        centeredFragment = (FrameLayout) findViewById(R.id.centeredFragment);
        progressBar = (RelativeLayout) findViewById(R.id.loadingBar);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.view_shake_anim);
        songInfoTextView = (TextView) findViewById(R.id.songInfoTextView);
        searchButton = (Button)findViewById(R.id.searchButton);
        playListLayout = (FrameLayout)findViewById(R.id.playListLayout);
        searchButton.setOnClickListener(this);

        songInfoTextView.setGravity(View.GONE);
        titleTextView.setText("音乐大厅");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        randomSongSuccessListener = getRandomSongSuccessListener();
        randomSongErrorListener = getRandomSongErrorListener();

        localImageViewPageFragment = new LocalImageViewPageFragment();
        localImageViewPageFragment.setOnItemClickListener(this);
        fragmentManager.beginTransaction()
                .replace(R.id.localImageViewPage, localImageViewPageFragment).commit();

        playSongFragmnet = new PlaySongFragmnet(this);
        fragmentManager.beginTransaction()
                .replace(R.id.currentPlayLayout, playSongFragmnet).commit();

        playListFragment = new PlayListFragment(this);
        fragmentManager.beginTransaction()
                .replace(R.id.playListLayout, playListFragment).commit();
        playListLayout.setVisibility(View.INVISIBLE);
        startLoading();
        randomSong(5);
    }

    private void resetCenterFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.centeredFragment, getClassifyButtonFragment()).commit();
    }

    private ClassifyButtonFragment getClassifyButtonFragment() {
        ClassifyButtonFragment fragment = new ClassifyButtonFragment(getRandomLayoutId(), randomSongList);
        fragment.setOnUpdateUIListener(this);
        return fragment;
    }

    private int getRandomLayoutId() {
        Random ran = new Random(System.currentTimeMillis());
        int pos = ran.nextInt(classifyButtonLayoutList.length);
        return classifyButtonLayoutList[pos];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            if (Math.abs(values[0]) > 17 ||
                    Math.abs(values[1]) > 17 ||
                    Math.abs(values[2]) > 17) {
                randomSongList.clear();
                randomSong(MAX_RANDOM_COUNT);
                vibrator.vibrate(100);
                centeredFragment.startAnimation(shakeAnimation);
                startLoading();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 随机歌曲
     *
     * @param count
     */
    private void randomSong(int count) {
        for (int i = 0; i < count; i++) {
            int type = randomSongType();
            int songIndex = randomSongIndex(type);
            String url = Const.RANK_LIST_SONG_URL + "&type=" + type + "&size=1" + "&offset=" + songIndex;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, randomSongSuccessListener, randomSongErrorListener);
            CMApplication.getInstance().addToRequestQueue(stringRequest);
        }
    }

    private int randomSongType() {
        return Const.RANDOM_TYPE_LIST[random.nextInt(Const.RANDOM_TYPE_LIST.length)];
    }

    private Random random = new Random(System.currentTimeMillis());
    private int randomSongIndex(int typeID) {
        return random.nextInt(100);
    }

    private Response.Listener<String> getRandomSongSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                BaiduRankSongJson baiduRankSongJson = gson.fromJson(s, BaiduRankSongJson.class);
                if (baiduRankSongJson.getSong_list().isEmpty()) {
                    randomSong(1);
                    return;
                }
                Song song = new Song(baiduRankSongJson.getSong_list().get(0));
                if (! randomSongList.contains(song)) {
                    randomSongList.add(song);
                }
                //保存数据
                if (randomSongList.size() == MAX_RANDOM_COUNT) {
                    stopLoading();
                    resetCenterFragment();
                }
            }
        };
    }

    private Response.ErrorListener getRandomSongErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //重新获取
                randomSong(1);
            }
        };
    }

    @Override
    public void onButtonLongTouch(Song song) {
        songInfoTextView.setVisibility(View.VISIBLE);
        songInfoTextView.setText("歌曲名：" + song.getSongName() + "\n"
                + "演唱者：" + song.getSinger());
    }

    @Override
    public void onButtonTouchUp() {
        songInfoTextView.setVisibility(View.GONE);
    }

    @Override
    public void onButtonClicked(Song song) {
        //播放歌曲
        new GetSongInfoRequest(song, new GetSongInfoRequest.IOnGetSongInfoListener() {
            @Override
            public void onSuccess(Song song) {
                PlayerManager.getmInstance().addSongToList(song, true);
                AppTools.showToast("已添加至播放列表", HomeActivity.this);
            }

            @Override
            public void onFailed(Song song) {
                AppTools.showToast("获取歌曲 " + song.getSongName() + " 信息失败~请重新添加", HomeActivity.this);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton) {
            //启动搜索界面
            SearchActivity.startActivity(this);
        }
    }

    @Override
    public void onItemClicked(int rankType) {
        //启动排行榜页面
        RankActivity.startActivity(this, rankType);
    }

    @Override
    public void onPlayListButtonClicked() {
        if (! playListLayout.isShown()) {
            //展示播放列表
            playListLayout.setVisibility(View.VISIBLE);
            playListFragment.reInit();
        } else {
            //隐藏播放列表
            playListLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onHidePlayListFragment() {
        playListLayout.setVisibility(View.INVISIBLE);
    }
}
