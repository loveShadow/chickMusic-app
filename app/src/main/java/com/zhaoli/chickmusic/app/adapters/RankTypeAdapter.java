package com.zhaoli.chickmusic.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.zhaoli.chickmusic.R;

/**
 * Created by zhaoli on 2015/10/20.
 */
public class RankTypeAdapter extends RecyclerView.Adapter<RankTypeAdapter.RankTypeHolder> {

    private Context context = null;
    private String[] ranktypeText = null;
    private int[] ranktypeIds = null;

    private int itemHeight;

    public RankTypeAdapter(Context context, String[] ranktypeText, int[] ranktypeIds) {
        this.context = context;
        this.ranktypeText = ranktypeText;
        this.ranktypeIds = ranktypeIds;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public RankTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_type_item_layout, null);
        RankTypeHolder holder = new RankTypeHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RankTypeHolder holder, int position) {
        holder.contentTextView.setText(ranktypeText[position]);
        holder.itemView.setTag(ranktypeIds[position]);
    }

    @Override
    public int getItemCount() {
        if (ranktypeText == null || ranktypeIds == null) {
            return 0;
        }
        return Math.min(ranktypeIds.length, ranktypeText.length);
    }

    public class RankTypeHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        public TextView contentTextView;

        public RankTypeHolder(final View itemView) {
            super(itemView);
            itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    itemHeight = itemView.getMeasuredHeight();
                    return true;
                }
            });
            contentTextView = (TextView)itemView.findViewById(R.id.contentText);
            contentTextView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    contentTextView.setBackgroundResource(R.drawable.rank_type_item_pressed_background);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    contentTextView.setBackgroundResource(R.drawable.rank_type_item_nopressed_background);
                    break;
            }
            return true;
        }
    }
}
