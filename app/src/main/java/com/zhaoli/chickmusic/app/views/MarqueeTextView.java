package com.zhaoli.chickmusic.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zhaoli on 2015/10/17.
 */
public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
