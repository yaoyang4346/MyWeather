package com.app.chenyang.sweather.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.adapter.viewholder.TopicCityViewHolder;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.utils.BaseUtils;

/**
 * Created by chenyang on 2017/2/13.
 */

public class TopicCityAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return MyConst.CITY_LIST.length;
    }

    @Override
    public Object getItem(int i) {
        return MyConst.CITY_LIST[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TopicCityViewHolder viewHolder;
        if(view==null){
            view = View.inflate(BaseUtils.getContext(), R.layout.topic_city_item, null);
            viewHolder = new TopicCityViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (TopicCityViewHolder) view.getTag();
        }
        viewHolder.tv.setText(MyConst.CITY_LIST[i]);
        return view;
    }
}
