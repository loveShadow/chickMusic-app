package com.zhaoli.chickmusic.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/21.
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerListHolder>{

    private Context context = null;
    private List<Song> songList = null;
    private IOnItemClickListener onItemClickListener = null;

    public PlayerListAdapter(Context context) {
        this.context = context;
        songList = new ArrayList<>();
    }

    public PlayerListAdapter(Context context, IOnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        songList = new ArrayList<>();
    }

    public PlayerListAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = (songList == null) ? new ArrayList<Song>() : songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = (songList == null) ? new ArrayList<Song>() : songList;
    }

    @Override
    public PlayerListAdapter.PlayerListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playerlist_item_layout, null);
        PlayerListHolder holder = new PlayerListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PlayerListAdapter.PlayerListHolder holder, int position) {
        holder.itemView.setTag(songList.get(position));
        holder.itemViewId.setTag(position);
        Song currentSong = PlayerManager.getmInstance().getCurrentPlaySong();
        if (currentSong != null && currentSong.equals(songList.get(position))) {
            holder.itemImageView.setVisibility(View.VISIBLE);
            holder.itemViewId.setVisibility(View.INVISIBLE);
            String picUrl = songList.get(position).getSingerPicUrl();
            if (picUrl != null &&
                   ! picUrl.isEmpty()) {
                CMApplication.getInstance().getImageLoader().get(picUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.itemImageView.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }, DensityUtils.dp2px(context, 70), DensityUtils.dp2px(context, 70), ImageView.ScaleType.CENTER);
            }
            holder.itemSongName.setTextColor(Color.parseColor("#30A3F6"));
            holder.itemSingerName.setTextColor(Color.parseColor("#30A3F6"));
        } else {
            String id = String.valueOf(position + 1);
            if (id.length() == 1) {
                id  = "0" + id;
            }
            holder.itemImageView.setVisibility(View.INVISIBLE);
            holder.itemViewId.setVisibility(View.VISIBLE);
            holder.itemViewId.setText(id);
            holder.itemSongName.setTextColor(Color.parseColor("#000000"));
            holder.itemSingerName.setTextColor(Color.parseColor("#000000"));
        }
        holder.itemSongName.setText(songList.get(position).getSongName());
        holder.itemSingerName.setText(songList.get(position).getSingerName());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class PlayerListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView itemViewId = null;
        public ImageView itemImageView = null;
        public TextView itemSongName = null;
        public TextView itemSingerName = null;
        public Button deleteButton = null;

        public PlayerListHolder(View itemView) {
            super(itemView);
            itemViewId = (TextView)itemView.findViewById(R.id.playerlist_itemid);
            itemImageView = (ImageView)itemView.findViewById(R.id.playerlist_image);
            itemSongName = (TextView)itemView.findViewById(R.id.playerlist_songname);
            itemSingerName = (TextView)itemView.findViewById(R.id.playerlist_singername);
            deleteButton = (Button)itemView.findViewById(R.id.playerlist_delete);
            deleteButton.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (deleteButton == v) {
                Song song = (Song)itemView.getTag();
                PlayerManager.getmInstance().deleteSongFromList(song);
                PlayerListAdapter.this.songList.remove(song);
                PlayerListAdapter.this.notifyDataSetChanged();
                showToast(song);
            } else if (itemView == v && onItemClickListener != null) {
                onItemClickListener.onClicked(v, (Song)itemView.getTag());
            }
        }

        private void showToast(Song song) {
            Toast.makeText(context, "已将" + song.getSingerName() + " - " + song.getSongName() + "移除", Toast.LENGTH_SHORT).show();
        }
    }
}
