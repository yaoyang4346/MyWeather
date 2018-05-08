package com.app.chenyang.sweather.adapter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.adapter.viewholder.SearchCityViewHolder;
import com.app.chenyang.sweather.entity.SearchCityInfo;
import com.app.chenyang.sweather.utils.BaseUtils;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/2/14.
 */

public class SearchCityListAdapter extends BaseAdapter{
    private ArrayList<SearchCityInfo> list;
    private String key;
    private String name;
    private int length;
    private SpannableStringBuilder style;

    public SearchCityListAdapter(ArrayList<SearchCityInfo> list, String key){
        this.list = list;
        this.key = key;
        length = key.length();
    }

    public ArrayList<SearchCityInfo> getList() {
        return list;
    }

    public SearchCityListAdapter setList(ArrayList<SearchCityInfo> list) {
        this.list = list;
        return this;
    }

    public String getKey() {
        return key;
    }

    public SearchCityListAdapter setKey(String key) {
        this.key = key;
        length = key.length();
        return this;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SearchCityInfo getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SearchCityViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(BaseUtils.getContext()).inflate(R.layout.search_city_item,null,false);
            viewHolder = new SearchCityViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (SearchCityViewHolder) view.getTag();
        }
        name = getItem(i).getName();
        style = new SpannableStringBuilder(name);
        int start = name.indexOf(key);
        int end = start + length;
        style.setSpan(new ForegroundColorSpan(BaseUtils.getContext().getResources().getColor(R.color.searchCityLine1Light))
                ,start,end,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        viewHolder.tvLine1.setText(style);
        viewHolder.tvLine2.setText(getItem(i).getProvince());
        return view;
    }
}
