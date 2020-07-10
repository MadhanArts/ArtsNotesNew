package com.madhanarts.artsnotes;

import android.view.View;
import android.view.ViewConfiguration;

public abstract class DoubleClickListener implements View.OnClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = ViewConfiguration.getDoubleTapTimeout();

    long lastClickTime = 0;

    @Override
    public void onClick(View v) {

        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA)
        {
            onDoubleClick(v);
            lastClickTime = 0;
        }
        else
        {
            onSingleClick(v);
        }
        lastClickTime = clickTime;

    }

    public abstract void onSingleClick(View v);
    public abstract void onDoubleClick(View v);

}
