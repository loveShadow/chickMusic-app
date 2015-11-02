package com.zhaoli.chickmusic.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.zhaoli.chickmusic.app.dialogs.base.BaseDialog;

/**
 * Created by zhaoli on 2015/10/26.
 */
public class ConfirmClearListDialog extends BaseDialog {

    /**
     * 方法执行顺序：
     * 显示： constrouctor --> show --> onCreate --> onStart --> onAttachToWindow
     * 销毁： dismiss --> onDetachedFromWindow --> onStop
     *
     * @param context
     */
    public ConfirmClearListDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        return null;
    }

    @Override
    public boolean setUiBeforShow() {
        return false;
    }
}
