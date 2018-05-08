package com.app.chenyang.sweather.helper.RecycleSlideHelp;

import android.animation.Animator;
import android.animation.ValueAnimator;

import com.app.chenyang.sweather.CityManageActivity;

/**
 * Created by chenyang on 2017/4/26.
 */

public class SlideAnimationHelper {
    public static final int STATE_CLOSE = CityManageActivity.NORMAL_MODE;
    public static final int STATE_OPEN = CityManageActivity.EDIT_MODE;

    private static int mCurrentState = STATE_CLOSE;

    private ValueAnimator mValueAnimator;

    public void openAnimation(long duration, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        mCurrentState = STATE_OPEN;
        setValueAnimator(duration, animatorUpdateListener, null);
    }

    public void closeAnimation(long duration, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        mCurrentState = STATE_CLOSE;
        setValueAnimator(duration, animatorUpdateListener, null);
    }

    private void setValueAnimator(long duration, ValueAnimator.AnimatorUpdateListener animatorUpdateListener,
                                  Animator.AnimatorListener listener) {
        mValueAnimator = getAnimation();
        mValueAnimator.setDuration(duration);

        if (animatorUpdateListener != null) {
            mValueAnimator.addUpdateListener(animatorUpdateListener);
        }
        if (listener != null) {
            mValueAnimator.addListener(listener);
        }
        start();
    }

    public ValueAnimator getAnimation() {
        if (mValueAnimator == null) {
            mValueAnimator = new ValueAnimator();
            mValueAnimator.setFloatValues(0.0f, 1.0f);
        }
        return mValueAnimator;
    }

    private void start() {
        if (mValueAnimator != null && !mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
    }

    public int getState() {
        return mCurrentState;
    }
}
