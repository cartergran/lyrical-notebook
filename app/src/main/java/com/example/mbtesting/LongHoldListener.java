package com.example.mbtesting;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class LongHoldListener implements GestureDetector.OnGestureListener {

    static boolean isLongPress = false;

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        isLongPress = false;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        isLongPress = true;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
