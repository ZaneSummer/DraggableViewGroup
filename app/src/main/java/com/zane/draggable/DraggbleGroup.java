package com.zane.draggable;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by zhao.wenchao on 2016/5/10.
 * email: zhao.wenchao@jyall.com
 * introduce:
 */
public class DraggbleGroup extends RelativeLayout {
    private ViewDragHelper mDragger;
    private View mAutoBackView;
    int screenWidth;
    int screenHeight;
    int result;
    private Point mAutoBackOriginPos = new Point();

    public DraggbleGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setGravity(Gravity.BOTTOM);
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
        screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
        result = 0;
        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - mAutoBackView.getWidth();
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;

            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - mAutoBackView.getHeight();
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                return newTop;

            }


            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //mAutoBackView手指释放时可以自动回去
                int[] location = new int[2];
                releasedChild.getLocationOnScreen(location);
//                Log.i("info", "x=" + location[0] + "y=" + location[1] + ",posx=" + mAutoBackOriginPos.x + ",posy=" + mAutoBackOriginPos.y + "w=" + mAutoBackView.getWidth());
                if (location[0] > -1 && location[0] < screenWidth / 2) {

                    mAutoBackOriginPos.x = 0;
                } else {
                    mAutoBackOriginPos.x = screenWidth - (mAutoBackView.getWidth());
                }
                mAutoBackOriginPos.y = location[1] - result;
                if (releasedChild == mAutoBackView) {
                    mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }


        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mAutoBackView = getChildAt(0);
    }
}
