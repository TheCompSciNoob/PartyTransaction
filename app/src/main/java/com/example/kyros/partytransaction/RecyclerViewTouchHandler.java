package com.example.kyros.partytransaction;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Kyros on 12/31/2017.
 */

public class RecyclerViewTouchHandler implements RecyclerView.OnItemTouchListener {

    private GestureDetector detector;
    private OnRecyclerViewTouchListener listener;

    public RecyclerViewTouchHandler(final RecyclerView rv, final OnRecyclerViewTouchListener listener) {
        this.listener = listener;
        detector = new GestureDetector(rv.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && listener != null) {
                    listener.onLongClick(child, rv.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && listener != null) {
            listener.onClick(child, rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        //nothing
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //nothing
    }

    public void setOnRecyclerViewTouchListener(OnRecyclerViewTouchListener listener)
    {
        this.listener = listener;
    }

    public interface OnRecyclerViewTouchListener {

        public void onClick(View child, int position);

        public void onLongClick(View child, int position);
    }
}
