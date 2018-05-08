package com.app.chenyang.sweather.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.adapter.viewholder.LivingDetailViewHolder;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.utils.BaseUtils;

import java.util.ArrayList;

/**
 * Created by chenyang on 2017/3/7.
 */

public class LivingDetailAdapter extends BaseAdapter {
    private ArrayList<String> contentList;
    private String[] livingDetailTitle;
    public LivingDetailAdapter(ArrayList<String> contentList){
        this.contentList = contentList;
        livingDetailTitle = BaseUtils.getContext().getResources().getStringArray(R.array.living_detail_title);
    }
    @Override
    public int getCount() {
        return livingDetailTitle.length;
    }

    @Override
    public String getItem(int i) {
        return livingDetailTitle[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LivingDetailViewHolder viewHolder;
        if(view == null){
            view = View.inflate(BaseUtils.getContext(), R.layout.living_detail_item,null);
            viewHolder = new LivingDetailViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (LivingDetailViewHolder) view.getTag();
        }
        viewHolder.tvLivingTitle.setText(getItem(i));
        viewHolder.tvLivingContent.setText(contentList.get(i));
        viewHolder.ivLivingIcon.setImageResource(MyConst.LIVING_DETAIL_ICON[i]);
        return view;
    }
}
