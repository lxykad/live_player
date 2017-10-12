package com.ijk.live.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ijk.live.application.BaseApplication;
import com.ijk.live.utils.Tool;

public class CustomScrollLayout extends RelativeLayout implements GestureDetector.OnGestureListener {

    private int lastX;
    private int lastY;

    private int screenWidth;
    private int screenHeight;
    private int statusBarHeight;

    private GestureDetector mDetector;

    public CustomScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomScrollLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        screenWidth = Tool.getScreenWidth(BaseApplication.getContext());
        screenHeight = Tool.getScreenHeight(BaseApplication.getContext());
        statusBarHeight = Tool.getStatusBarHeight(BaseApplication.getContext());

        mDetector = new GestureDetector(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event == null) {
            return  false;
        }

        mDetector.onTouchEvent(event);

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        lastX = (int) e.getRawX();
        lastY = (int) e.getRawY();

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mListener.click();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent event, float distanceX, float distanceY) {

        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        //计算移动的距离
        int offsetX = x - lastX;
        int offsetY = y - lastY;

        //使用LayoutParams
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
        int leftMargin = layoutParams.leftMargin + offsetX;
        int topMargin = layoutParams.topMargin + offsetY;

        int limitV = screenWidth - layoutParams.width;
        int limitH = screenHeight - statusBarHeight - layoutParams.height;

        if (leftMargin < 0) {
            leftMargin = 0;
        } else if (leftMargin > limitV) {// 左右margin
            leftMargin = limitV;
        }

        // 上下margin
        if (topMargin < 0) {
            topMargin = 0;
        } else if (topMargin > limitH) {
            topMargin = limitH;
        }

        layoutParams.leftMargin = leftMargin;
        layoutParams.topMargin = topMargin;
        setLayoutParams(layoutParams);

        lastX = x;
        lastY = y;

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        return false;
    }

    private ClickListener mListener;

    public void setclick(ClickListener listener) {
        mListener = listener;
    }

    public interface ClickListener {
        void click();
    }
}