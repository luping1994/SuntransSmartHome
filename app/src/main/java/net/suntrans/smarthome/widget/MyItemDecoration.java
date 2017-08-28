package net.suntrans.smarthome.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.suntrans.smarthome.utils.UiUtils;


/**
 * Created by Looney on 2017/8/28.
 */

public class MyItemDecoration extends RecyclerView.ItemDecoration {
    private Paint dividerPaint;
    private int dividerHeight = UiUtils.dip2px(1);
    public MyItemDecoration() {
        this.dividerPaint = new Paint();
        dividerPaint.setColor(Color.parseColor("#f1f1f1"));
        dividerPaint.setAntiAlias(true);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;//类似加了一个bottom padding
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, dividerPaint);
        }

    }
}
