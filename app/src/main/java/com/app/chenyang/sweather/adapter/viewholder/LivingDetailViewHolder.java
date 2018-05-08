package com.app.chenyang.sweather.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.chenyang.sweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyang on 2017/3/7.
 */

public class LivingDetailViewHolder {
    @BindView(R.id.tv_detail_title)
    public TextView tvLivingTitle;
    @BindView(R.id.iv_detail_icon)
    public ImageView ivLivingIcon;
    @BindView(R.id.tv_detail_content)
    public TextView tvLivingContent;

    public LivingDetailViewHolder(View view){
        ButterKnife.bind(this,view);
    }
}
