package com.zhaoli.chickmusic.app.dialogs.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.zhaoli.chickmusic.commonutils.phone.StatusBarUtils;

/**
 * Created by zhaoli on 2015/10/26.
 *
 * 对话框基础类
 */
public abstract class BaseDialog extends Dialog {
    private static final String TAG = BaseDialog.class.getSimpleName();
    /**
     * context 上下文
     */
    protected Context context;
    /**
     * 设备密度
     */
    protected DisplayMetrics dm;
    /**
     * 点击对话框以外区域是否销毁
     */
    protected boolean outTouchIsDismiss;
    /**
     * 最底部View（Dialog容器）
     */
    protected LinearLayout rootView;
    /**
     * 对话框View
     */
    protected LinearLayout dialogView;
    /**
     * 对话框最大高度（带背景的高度）
     */
    protected float maxHeight;
    /**
     * 对话框宽度比例（对话框宽 = 屏幕宽 * 比例）
     * 对话框高度比例（对话框高 = 最大高度 * 比例）
     */
    protected float widthScale;
    protected float heightScale;

    /**
     * 动画
     */
    protected BaseAnimatorSet showAnim;
    protected BaseAnimatorSet dismissAnim;
    /**
     * 是否正在执行动画
     */
    private boolean isShowingAnim;
    /**
     * 默认动画Listener
     */
    private Animator.AnimatorListener defaultAnimatorListener = null;
    protected Animator.AnimatorListener showAnimListener = null;
    protected Animator.AnimatorListener dismissAnimListener = null;

    /*********************************************显示对话框流程 start***************************************************/
    /**
     * 方法执行顺序：
     * 显示： constrouctor --> show --> onCreate --> onStart --> onAttachToWindow
     * 销毁： dismiss --> onDetachedFromWindow --> onStop
     * @param context
     */
    public BaseDialog(Context context) {
        super(context);
        Log.d(TAG, "constrouctor");

        initDialogTheme();
        this.context = context;
        this.dm = context.getResources().getDisplayMetrics();
        this.maxHeight = dm.heightPixels - StatusBarUtils.getHeight(context);
    }

    @Override
    public void show() {
        super.show();
        Log.d(TAG, "show");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        //初始化根部布局
        rootView = new LinearLayout(context);
        rootView.setGravity(Gravity.CENTER);
        //初始化对话框布局
        dialogView = new LinearLayout(context);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.addView(onCreateView());
        rootView.addView(dialogView);

        setContentView(rootView, new ViewGroup.LayoutParams(dm.widthPixels, (int) maxHeight));
        setCanceledOnTouchOutside(true);
        //设置点击其他部位销毁对话框
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outTouchIsDismiss) {
                    dismiss();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");

        setUiBeforShow();
        //设置对话框 宽/高
        int dialogHeight, dialogWidth;
        if (widthScale == 0) {
            dialogWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            dialogWidth = (int)(dm.widthPixels * widthScale);
        }
        if (heightScale == 0) {
            dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (heightScale == 1) {
            dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            dialogHeight = (int) (maxHeight * heightScale);
        }
        //设置rootView的布局参数
        dialogView.setLayoutParams(new LinearLayout.LayoutParams(dialogWidth, dialogHeight));
        //设置动画
        startShowAnim();
    }
    /*********************************************显示对话框流程 end***************************************************/

    /*********************************************销毁对话框流程 start***************************************************/
    @Override
    public void dismiss() {
        super.dismiss();
        Log.d(TAG, "dismiss");
        startDismissAnim();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
    /*********************************************销毁对话框流程 end***************************************************/

    /**
     * 初始化对话框主题
     */
    private void initDialogTheme() {
        //设置窗口无标题  对应 android:windowNoTitle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置背景透明  对应 android:windowBackground
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置对话框背景是否暗灰 对应 android:backgroundDimEnabled
        getWindow().addFlags(LayoutParams.FLAG_DIM_BEHIND);
    }

    private void startShowAnim() {
        if (showAnim != null) {
            if (showAnimListener != null) {
                showAnim.setAnimListener(showAnimListener);
            } else {
                showAnim.setAnimListener(getDefaultAnimatorListener());
            }
            showAnim.startAnim(dialogView);
        } else {
            dismissNoAnim();
        }
    }

    private void startDismissAnim() {
        if (dismissAnim != null) {
            if (dismissAnimListener != null) {
                dismissAnim.setAnimListener(dismissAnimListener);
            } else {
                dismissAnim.setAnimListener(getDefaultAnimatorListener());
            }
            dismissAnim.startAnim(dialogView);
        } else {
            dismissNoAnim();
        }
    }

    private Animator.AnimatorListener getDefaultAnimatorListener() {
        if (defaultAnimatorListener == null) {
            defaultAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isShowingAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShowingAnim = false;
                    dismissNoAnim();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isShowingAnim  = false;
                    dismissNoAnim();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        }
        return defaultAnimatorListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isShowingAnim) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (isShowingAnim) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 获取对话框布局
     * @return 对话框布局
     */
    public abstract View onCreateView();

    /**
     * 显示对话框之前处理界面或逻辑
     * @return
     */
    public abstract boolean setUiBeforShow();

    /****************************对外接口*****************************/
    /**
     * 显示带动画的对话框
     * @param animStyle
     */
    public void showByAnim(int animStyle) {
        Window window = getWindow();
        window.setWindowAnimations(animStyle);
        show();
    }

    /**
     * 无动画销毁
     */
    public void dismissNoAnim() {
        super.dismiss();
    }

    /**
     * 设置背景是否昏暗
     * @param dimEnable
     */
    public BaseDialog setDimEnable(boolean dimEnable) {
        if (dimEnable) {
            getWindow().addFlags(LayoutParams.FLAG_DIM_BEHIND);
        } else {
            getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
        }
        return this;
    }

    public BaseDialog setWidthScale(float widthScale) {
        this.widthScale = widthScale;
        return this;
    }

    public BaseDialog setHeightScale(float heightScale) {
        this.heightScale = heightScale;
        return this;
    }

    public BaseDialog setDialogWidth(int dialogWidth) {
        this.widthScale = dm.widthPixels / dialogWidth;
        return this;
    }

    public BaseDialog setDialogHeight(int dialogHeight) {
        this.widthScale = maxHeight / dialogHeight;
        return this;
    }

    public void setShowAnimListener(Animator.AnimatorListener showAnimListener) {
        this.showAnimListener = showAnimListener;
    }

    public void setDismissAnimListener(Animator.AnimatorListener dismissAnimListener) {
        this.dismissAnimListener = dismissAnimListener;
    }
}
