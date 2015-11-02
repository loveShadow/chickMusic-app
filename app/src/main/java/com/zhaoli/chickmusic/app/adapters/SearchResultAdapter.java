package com.zhaoli.chickmusic.app.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.eventbus.event.AddSongToListEvent;
import com.zhaoli.chickmusic.app.views.MarqueeTextView;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.utils.DensityUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhaoli on 2015/10/17.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>{

    private Context context = null;
    private List<Song> songList = new ArrayList<>();
    private int[] backGroundColors = null;
    private Random random = null;

    private IOnItemClickListener onItemClickListener = null;

    public SearchResultAdapter(Context context) {
        this(context, null, null);
    }

    public SearchResultAdapter(Context context, IOnItemClickListener onItemClickListener) {
        this(context, null, onItemClickListener);
    }

    public SearchResultAdapter(Context context, List<Song> songList, IOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.songList = (songList == null) ? new ArrayList<Song>() : songList;
        random = new Random(System.currentTimeMillis());
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.item_color_array);
        backGroundColors = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i ++) {
            backGroundColors[i] = typedArray.getColor(i, 0);
        }
    }

    public void setData(List<Song> songList) {
        this.songList = (songList == null) ? new ArrayList<Song>() : songList;
    }

    public void addData(List<Song> songList) {
        this.songList.addAll(songList);
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_item_layout, parent, false);
        SearchResultViewHolder viewHolder = new SearchResultViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
        holder.itemView.setTag(songList.get(position));
        holder.singerNameTextView.setText(songList.get(position).getSinger());
        holder.songNameTextView.setText(songList.get(position).getSongName());
        String picUrl = songList.get(position).getSingerPicUrl();
        if (picUrl != null && ! picUrl.isEmpty()) {
            CMApplication.getInstance().getImageLoader().get(picUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.iconImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }, DensityUtils.dp2px(context, 70), DensityUtils.dp2px(context, 70), ImageView.ScaleType.CENTER);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public ImageView iconImage = null;
        public MarqueeTextView songNameTextView = null;
        public MarqueeTextView singerNameTextView = null;
        public Button addToPlayListButton = null;
        public Button downloadButton = null;

        private ImageView addImageView = null;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(backGroundColors[random.nextInt(backGroundColors.length)]);
            gd.setCornerRadius(8);
            itemView.setBackgroundDrawable(gd);
            iconImage = (ImageView)itemView.findViewById(R.id.songImage);
            songNameTextView = (MarqueeTextView)itemView.findViewById(R.id.songNameText);
            singerNameTextView = (MarqueeTextView)itemView.findViewById(R.id.singerNameText);
            addToPlayListButton = (Button)itemView.findViewById(R.id.addToPlayListButton);
            addToPlayListButton.setOnClickListener(this);
            downloadButton = (Button)itemView.findViewById(R.id.downLoadButton);
            downloadButton.setOnClickListener(this);

            addImageView = new ImageView(context);
            addImageView.setBackgroundResource(R.mipmap.add_to_playlist_icon);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == addToPlayListButton) {
                int[] startPos = new int[]{0, 0};
                view.getLocationInWindow(startPos);
                CMApplication.getInstance().getEventBus().post(new AddSongToListEvent(startPos, (Song)itemView.getTag()));
            } else if (view == downloadButton) {

            } else if (view == itemView) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClicked(view, (Song)itemView.getTag());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == itemView) {
                if (onItemClickListener != null) {
                    onItemClickListener.onLongClicked(v, (Song) itemView.getTag());
                }
            }
            return false;
        }
    }
}
