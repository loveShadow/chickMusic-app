package com.zhaoli.chickmusic.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.utils.DensityUtils;

/**
 * Created by zhaoli on 2015/10/15.
 *
 * 注意：设置背景必须设置android:background属性
 *
 *  且不规则部位为透明
 *
 */
public class IrregularButton extends RelativeLayout implements View.OnTouchListener , View.OnLongClickListener{

    private Bitmap bitmap = null;
    private Animation clickAnimation = null;
    private IOnButtonClickedListener clickedListener = null;

    private TextView buttonText = null;

    int marginBottom = -1;
    int marginTop = -1;

    public interface IOnButtonClickedListener {
        void onClicked(View v);
        void onTouchUp(View v);
    }

    public IrregularButton(Context context) {
        this(context, null);
    }

    public IrregularButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public IrregularButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(attrs);
    }

    public void setOnButtonClickedListener(IOnButtonClickedListener listener) {
        clickedListener = listener;
    }

    public void setButtonText(String text) {
        buttonText.setText(text);
    }

    private void initButton(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.irregular_button_layout, this);
        buttonText = (TextView)findViewById(R.id.buttonText);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IrregularButton);
        if (typedArray != null) {
            marginBottom = (int)typedArray.getDimension(R.styleable.IrregularButton_marginBottom, -1f);
            marginTop = (int)typedArray.getDimension(R.styleable.IrregularButton_marginTop, -1f);
            typedArray.recycle();

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(DensityUtils.dp2px(getContext(), 70), ViewGroup.LayoutParams.WRAP_CONTENT);
            if (marginBottom != -1 || marginTop != -1) {
                lp.setMargins(0, ((marginTop != -1) ? marginTop : 0), 0, ((marginBottom != -1) ? marginBottom : 0));
                lp.addRule(CENTER_HORIZONTAL | ALIGN_PARENT_BOTTOM);
                buttonText.setLayoutParams(lp);
                buttonText.setGravity(Gravity.CENTER);
            }
        }

        bitmap = ((BitmapDrawable)getBackground()).getBitmap();
        clickAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.irregular_button_scale_anim);
        setOnTouchListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        if (x < 0 || y < 0 || x > bitmap.getWidth() || y > bitmap.getHeight() ||
                bitmap.getPixel(x, y) == 0) {
            //如果是在别的地方松开，也要处理touch事件
            if ((event.getAction() == MotionEvent.ACTION_CANCEL ||
                    event.getAction() == MotionEvent.ACTION_UP) && clickedListener != null) {
                clickedListener.onTouchUp(v);
            }
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                IrregularButton.this.startAnimation(clickAnimation);
                break;
            case MotionEvent.ACTION_UP:
                if (clickedListener != null) {
                    clickedListener.onClicked(v);
                    clickedListener.onTouchUp(v);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (clickedListener != null) {
                    clickedListener.onTouchUp(v);
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {

        return false;
    }
}
