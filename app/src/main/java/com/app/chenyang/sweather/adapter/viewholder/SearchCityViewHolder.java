package com.app.chenyang.sweather.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.app.chenyang.sweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyang on 2017/2/14.
 */

public class SearchCityViewHolder {
    @BindView(R.id.search_city_line1)
    public TextView tvLine1;
    @BindView(R.id.search_city_line2)
    public TextView tvLine2;

    public SearchCityViewHolder(View view){
        ButterKnife.bind(this,view);
    }
}
