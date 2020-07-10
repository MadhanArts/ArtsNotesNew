package com.madhanarts.artsnotes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class LinedEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Paint mPaint = new Paint();

    public LinedEditText(Context context) {
        super(context);
    }

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint()
    {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0x80000000);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int left = getLeft();
        int right = getRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            paddingLeft = getPaddingStart();
        }
        else
        {
            paddingLeft = getPaddingLeft();
        }
        int paddingRight = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            paddingRight = getPaddingEnd();
        }
        else
        {
            paddingRight = getPaddingRight();
        }
        int height = getHeight();
        int lineHeight = getLineHeight();
        int count = getLineCount();

        for (int i = 0; i < count; i++)
        {
            int baseLine = lineHeight * (i+1) + paddingTop;

            canvas.drawLine(paddingLeft, baseLine, right - paddingRight, baseLine, mPaint);

        }

        super.onDraw(canvas);
    }
}
