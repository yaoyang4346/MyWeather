package com.app.chenyang.sweather.helper.RecycleViewHelp;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chenyang on 2017/4/10.
 */

public class OnRecycleItemTouchListener extends GestureDetector.SimpleOnGestureListener implements RecyclerView.OnItemTouchListener{
    private RecyclerView recyclerView;
    private GestureDetector gestureDetector;

    public OnRecycleItemTouchListener(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
        gestureDetector = new GestureDetector(recyclerView.getContext(),this);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child!=null) {
            RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
            onItemClick(vh,recyclerView.getChildAdapterPosition(child));
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child!=null) {
            RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
            onItemLongClick(vh,recyclerView.getChildAdapterPosition(child));
        }
    }

    public void onItemLongClick(RecyclerView.ViewHolder vh, int position){}
    public void onItemClick(RecyclerView.ViewHolder vh,int position){}
}
