package com.app.chenyang.sweather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by chenyang on 2017/3/30.
 */

public class RotateLoading extends View{

    private static final int DEFAULT_WIDTH = 6;
    private static final int DEFAULT_SHADOW_POSITION = 2;
    private static final int DEFAULT_SPEED_OF_DEGREE = 7;

    private int[] colors = { Color.parseColor("#FE4365"), Color.parseColor("#DE7D2C"), Color.parseColor("#458994")
                            , Color.parseColor("#23EBBA"), Color.parseColor("#3EBCCA"), Color.parseColor("#773460")};

    private Paint mPaint;
    private Paint mShadowPaint;

    private RectF loadingRectF;
    private RectF shadowRectF;

    private int topDegree = 10;
    private int bottomDegree = 190;

    private float arc;

    private int width;

    private boolean changeBigger = true;

    private int shadowPosition;

    private int color;

    private int speedOfDegree;

    private float speedOfArc;

    private int flag = 0;

    public RotateLoading(Context context) {
        super(context);
        initView(context, null);
    }

    public RotateLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RotateLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        color = colors[0];
        width = dpToPx(context, DEFAULT_WIDTH);
        shadowPosition = dpToPx(getContext(), DEFAULT_SHADOW_POSITION);
        speedOfDegree = DEFAULT_SPEED_OF_DEGREE;

        speedOfArc = speedOfDegree / 4;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mShadowPaint = new Paint();
        mShadowPaint.setColor(Color.parseColor("#1a000000"));
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setStrokeWidth(width);
        mShadowPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        arc = 10;

        loadingRectF = new RectF(2 * width, 2 * width, w - 2 * width, h - 2
                * width);
        shadowRectF = new RectF(2 * width + shadowPosition, 2 * width
                + shadowPosition, w - 2 * width + shadowPosition, h - 2 * width
                + shadowPosition);
    }

    private boolean isChange = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(shadowRectF, topDegree, arc, false, mShadowPaint);
        canvas.drawArc(shadowRectF, bottomDegree, arc, false, mShadowPaint);

        canvas.drawArc(loadingRectF, topDegree, arc, false, mPaint);
        canvas.drawArc(loadingRectF, bottomDegree, arc, false, mPaint);

        topDegree += speedOfDegree;
        bottomDegree += speedOfDegree;
        if (topDegree > 360) {
            topDegree = topDegree - 360;
        }
        if (bottomDegree > 360) {
            bottomDegree = bottomDegree - 360;
        }

        if (changeBigger) {
            if (isChange) {
                isChange = !isChange;
                flag++;
                mPaint.setColor(colors[flag% colors.length]);
            }
            if (arc < 160) {
                arc += speedOfArc;
                invalidate();
            }
        } else {
            if (!isChange) {
                isChange = !isChange;
            }
            if (arc > speedOfDegree) {
                arc -= 2 * speedOfArc;
                invalidate();
            }
        }
        if (arc >= 160 || arc <= 10) {
            changeBigger = !changeBigger;
            invalidate();
        }
    }

    public void setLoadingColor(int color) {
        this.color = color;
    }

    public int getLoadingColor() {
        return color;
    }

    public int dpToPx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
