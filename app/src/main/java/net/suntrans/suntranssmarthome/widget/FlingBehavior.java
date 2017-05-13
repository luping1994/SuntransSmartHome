package net.suntrans.suntranssmarthome.widget;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

/**
 * Created by Looney on 2017/4/24.
 */

public final class FlingBehavior extends AppBarLayout.Behavior {
    private boolean b;

    public FlingBehavior() {
    }

    public FlingBehavior(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (dy > 0) {
        }
        this.b = true;
        return;
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        if (((velocityY > 0.0F) && (!this.b)) || ((velocityY < 0.0F) && (this.b))) {
            velocityY *= -1.0F;
        }

        if (((target instanceof NestedScrollView)) && (Math.abs(velocityY) > 8000.0F)) {
            consumed = false;
        }

        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

}


