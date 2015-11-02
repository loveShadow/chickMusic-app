package com.zhaoli.chickmusic.app.dialogs.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by zhaoli on 2015/10/26.
 *
 * 针对对话框动画基础类
 */
public abstract class BaseAnimatorSet {
    /**
     * 动画时长 默认 500ms
     */
    protected long duration = 500;
    protected AnimatorSet animatorSet = new AnimatorSet();

    private Interpolator interpolator;
    private long delay;
    private Animator.AnimatorListener animatorListener;

    /**
     * 设置动画
     * @param view  展示动画的布局
     */
    public abstract void setAnimatorSet(View view);

    /**
     * 启动动画
     * @param view
     */
    protected void startAnim(View view) {
        resetAnim(view);
        setAnimatorSet(view);

        animatorSet.setDuration(duration);
        if (interpolator != null) {
            animatorSet.setInterpolator(interpolator);
        }
        if (delay > 0) {
            animatorSet.setStartDelay(delay);
        }
        animatorSet.start();
    }

    /**
     * 清除View的动画
     * @param view
     */
    public static void resetAnim(View view) {
        view.setAlpha(1);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setRotation(0);
        view.setRotationX(0);
        view.setRotationY(0);
    }

    /**
     * 设置动画时长
     * @param duration
     * @return
     */
    public BaseAnimatorSet setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 设置动画延迟时间
     * @param delay
     */
    public BaseAnimatorSet setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    /**
     * 设置动画插补器
     * @param interpolator
     */
    public BaseAnimatorSet setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public BaseAnimatorSet setAnimListener(Animator.AnimatorListener animListener) {
        animatorSet.addListener(animListener);
        return this;
    }
}
