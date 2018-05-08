package com.app.chenyang.sweather.adapter.viewholder;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.helper.RecycleSlideHelp.ISlide;
import com.app.chenyang.sweather.helper.RecycleSlideHelp.SlideAnimationHelper;
import com.app.chenyang.sweather.ui.widget.SmoothCheckBox;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyang on 2017/4/10.
 */

public class CityManagerViewHolder extends RecyclerView.ViewHolder implements ISlide{
    private static final int OFFSET = BaseUtils.dpToPx(40);
    private static final int DURATION_OPEN = 300;
    private static final int DURATION_CLOSE = 300;

    @BindView(R.id.tv_city) public TextView tvCity;
    @BindView(R.id.tv_province) public TextView tvProvince;
    @BindView(R.id.tv_time) public TextView tvTime;
    @BindView(R.id.cb_item) public SmoothCheckBox cbItem;
    @BindView(R.id.iv_weather_icon) public ImageView ivIcon;
    @BindView(R.id.tv_temp) public TextView tvTemp;
    @BindView(R.id.tv_max_min) public TextView tvMaxMin;
    @BindView(R.id.iv_sort) public ImageView ivSort;
    @BindView(R.id.ll_left) public LinearLayout lLeft;
    @BindView(R.id.ll_right) public  LinearLayout lRight;

    private OpenUpdateListener mOpenUpdateListener;
    private CloseUpdateListener mCloseUpdateListener;
    private SlideAnimationHelper mSlideAnimationHelper;


    public CityManagerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        mSlideAnimationHelper = new SlideAnimationHelper();
    }

    @Override
    public void open() {
        if (mOpenUpdateListener == null) {
            mOpenUpdateListener = new OpenUpdateListener();
        }
        mSlideAnimationHelper.openAnimation(DURATION_OPEN, mOpenUpdateListener);
    }

    @Override
    public void close() {
        if (mCloseUpdateListener == null) {
            mCloseUpdateListener = new CloseUpdateListener();
        }
        mSlideAnimationHelper.closeAnimation(DURATION_CLOSE, mCloseUpdateListener);
    }

    private class OpenUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override public void onAnimationUpdate(ValueAnimator animation) {
            float fraction = animation.getAnimatedFraction();
            int endX = (int) (OFFSET * fraction);
            cbItem.setChecked(false);
            doAnimationSet(endX, fraction);
        }
    }

    private class CloseUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float fraction = animation.getAnimatedFraction();
            fraction = 1.0f - fraction;
            int endX = (int) (OFFSET * fraction);

            doAnimationSet(endX, fraction);
        }
    }

    public void doAnimationSet(int offset, float fraction){
        lLeft.scrollTo(-offset, 0);
        lRight.scrollTo(offset,0);
        cbItem.setVisibility(View.VISIBLE);
        ivSort.setVisibility(View.VISIBLE);

        cbItem.setScaleX(fraction);
        cbItem.setScaleY(fraction);
        cbItem.setAlpha(fraction * 255);

        ivSort.setScaleX(fraction);
        ivSort.setScaleY(fraction);
        ivSort.setAlpha(fraction * 255);

    }
    public void onBindSlide() {
        switch (mSlideAnimationHelper.getState()) {
            case SlideAnimationHelper.STATE_CLOSE:
                lLeft.scrollTo(0, 0);
                lRight.scrollTo(0, 0);
                cbItem.setVisibility(View.GONE);
                ivSort.setVisibility(View.GONE);
                break;

            case SlideAnimationHelper.STATE_OPEN:
                cbItem.setChecked(false,false);
                lLeft.scrollTo(-OFFSET, 0);
                lRight.scrollTo(OFFSET, 0);
                cbItem.setVisibility(View.VISIBLE);
                ivSort.setVisibility(View.VISIBLE);
                break;
        }
    }
}
