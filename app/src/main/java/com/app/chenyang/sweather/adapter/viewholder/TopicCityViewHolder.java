package com.app.chenyang.sweather.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.app.chenyang.sweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyang on 2017/2/13.
 */

public class TopicCityViewHolder {
    @BindView(R.id.tv_topic_city)
    public TextView tv;

    public TopicCityViewHolder(View view){
        ButterKnife.bind(this,view);
    }
}
