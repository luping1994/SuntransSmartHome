package net.suntrans.suntranssmarthome.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Looney on 2017/4/20.
 */

public class MyHomeViewPager extends ViewPager {
    private boolean a = false;

    public MyHomeViewPager(Context context) {
        super(context);
    }

    public MyHomeViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
        if (this.a) {
            return super.onInterceptTouchEvent(paramMotionEvent);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (this.a) {
            return super.onTouchEvent(paramMotionEvent);
        }
        return false;
    }

    public void setScanScroll(boolean paramBoolean) {
        this.a = paramBoolean;
    }

    //去除页面切换时的滑动翻页效果
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, false);
    }
}
