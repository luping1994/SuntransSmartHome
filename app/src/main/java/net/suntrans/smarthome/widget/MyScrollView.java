package net.suntrans.smarthome.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import net.suntrans.smarthome.utils.LogUtil;

/**
 * Created by Looney on 2017/4/24.
 */

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // alpha = 滑出去的高度/(screenHeight/3);
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        int height = linearLayout.getChildAt(0).getHeight();
        float scrollY = getScrollY();//该值 大于0
        float alpha = scrollY / height;
        LogUtil.i("scrollY=" + scrollY + "  " + "height=" + height);

        if (alpha <= 1)
            translucentListener.onTranslucent(alpha);
    }

    public interface TranslucentListener {


        /**
         * 透明度的回调
         *
         * @param alpha
         */
        public void onTranslucent(float alpha);
    }

    public void setTranslucentListener(TranslucentListener translucentListener) {
        this.translucentListener = translucentListener;
    }

    private TranslucentListener translucentListener;



    private int mTopOverScrollDistance = 0;
    private int mBottomOverScrollDistance = 220;

    private boolean mTopOverScrollEnable=false;
    private boolean mBottomOverScrollEnable=true;

    @Override
    protected boolean overScrollBy(
            int deltaX, int deltaY,
            int scrollX, int scrollY,
            int scrollRangeX, int scrollRangeY,
            int maxOverScrollX, int maxOverScrollY,
            boolean isTouchEvent) {

        final int overScrollMode = getOverScrollMode();
        final boolean canScrollHorizontal =
                computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        final boolean canScrollVertical =
                computeVerticalScrollRange() > computeVerticalScrollExtent();
        final boolean overScrollHorizontal = overScrollMode == OVER_SCROLL_ALWAYS ||
                (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollHorizontal);
        final boolean overScrollVertical = overScrollMode == OVER_SCROLL_ALWAYS ||
                (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollVertical);

        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX = 0;
        }

        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            mTopOverScrollDistance = 0;
            mBottomOverScrollDistance = 0;
        }

        // Clamp values if at the limits and record
        final int left = -maxOverScrollX;
        final int right = maxOverScrollX + scrollRangeX;
        final int top = -mTopOverScrollDistance;
        final int bottom = mBottomOverScrollDistance + scrollRangeY;

        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }

        boolean clampedY = false;
        if (newScrollY > scrollRangeY) {    //判断是否超过底部
            if (!mBottomOverScrollEnable) {
                newScrollY = scrollRangeY;
                clampedY = true;
            }
        } else if (newScrollY < 0) {    //判断是否超过顶部
            if (!mTopOverScrollEnable) {
                newScrollY = 0;
                clampedY = true;
            }
        }

        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }

        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);

        return clampedX || clampedY;
    }
}
