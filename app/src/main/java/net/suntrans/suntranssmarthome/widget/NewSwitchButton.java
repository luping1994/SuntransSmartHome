package net.suntrans.suntranssmarthome.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import static com.tencent.bugly.crashreport.crash.c.g;
import static com.tencent.bugly.crashreport.crash.c.m;
import static net.suntrans.suntranssmarthome.R.attr.bottomDrawable;

/**
 * Created by Looney on 2017/4/26.
 */

public class NewSwitchButton extends CompoundButton {
    private static final String TAG = "NewSwitchButton";
    private Drawable mBottomDrawable;
    private Drawable mThumbDrawable;
    private int mCheckedColor;
    private int mMargin;//n
    private int leftMin = 0;
    private int marginLeftMax;
    private int marginLeft = 0;//小圆距离左边的距离a
    private int scaledTouchSlop;


    private float mLastX;
    private float mCurrentX = 0;
    private int mMoveDeltX = 0;
    private boolean mIsScrolled = false;

    public NewSwitchButton(Context context) {
        this(context, null);
    }

    public NewSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NewSwitchButton, defStyleAttr, 0);

//        final Resources resources = context.getResources();


        mBottomDrawable = array.getDrawable(R.styleable.NewSwitchButton_bottomDrawable);
        mThumbDrawable= array.getDrawable(R.styleable.NewSwitchButton_thumbDrawable);

        this.mBottomDrawable.setBounds(getCompoundPaddingLeft(), getCompoundPaddingTop(),
                this.mBottomDrawable.getIntrinsicWidth() + getCompoundPaddingRight(),
                this.mBottomDrawable.getIntrinsicHeight() + getCompoundPaddingBottom());
        this.mBottomDrawable.setCallback(this);

        this.mThumbDrawable.setBounds(getCompoundPaddingLeft(), getCompoundPaddingTop(),
                this.mThumbDrawable.getIntrinsicWidth() + getCompoundPaddingRight(),
                this.mThumbDrawable.getIntrinsicHeight() + getCompoundPaddingBottom());
        this.mThumbDrawable.setCallback(this);

        mMargin = (int) array.getDimension(R.styleable.NewSwitchButton_margin, dip2px(context, 2));
        this.marginLeftMax = mBottomDrawable.getIntrinsicWidth() - mBottomDrawable.getIntrinsicHeight() - mMargin;
        if (isChecked()) {
            marginLeft = marginLeftMax;
//            marginLeft = mBottomDrawable.getIntrinsicWidth()-getCompoundPaddingRight()-marginLeftMax;
        }
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        array.recycle();


    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] arratOfInt = getDrawableState();
        if (mBottomDrawable != null) {
            mBottomDrawable.setState(arratOfInt);
        }
        if (mThumbDrawable != null) {
            mThumbDrawable.setState(arratOfInt);
        }
//        invalidate();
    }

    private int checkMarginLeft(int space) {
        if (space < leftMin)
            space = leftMin;
        if (space > marginLeftMax)
            space = marginLeftMax;
        marginLeft = space;
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SwitchCompat compat;
        int height = 0;
        if (mBottomDrawable != null) {
            height = mBottomDrawable.getIntrinsicHeight() - 2 * mMargin;

            if (!isChecked()) {
                mBottomDrawable.setColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.SRC_IN);
            } else {
                mBottomDrawable.setColorFilter(Color.parseColor("#007dff"), PorterDuff.Mode.SRC_IN);
            }
            mBottomDrawable.draw(canvas);
        }
        if (mThumbDrawable != null) {
            mThumbDrawable.setBounds(this.marginLeft + this.mMargin + getCompoundPaddingLeft(),
                    this.mMargin + getCompoundPaddingTop(),
                    height + marginLeft + mMargin + getCompoundPaddingRight(),
                    height + mMargin + getCompoundPaddingBottom());
            mThumbDrawable.draw(canvas);
        }
//        LogUtil.i("ondraw is runring");
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        if (widthMode == MeasureSpec.AT_MOST) {
        width = getCompoundPaddingLeft() + getCompoundPaddingRight() + mBottomDrawable.getIntrinsicWidth();
//        }
//        if (heightMode == MeasureSpec.AT_MOST) {
        height = getCompoundPaddingTop() + getCompoundPaddingBottom() + mBottomDrawable.getIntrinsicHeight();
//        }
        setMeasuredDimension(width, height);
    }

    /**
     * dip转换px
     */
    public static int dip2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {

            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
//                Log.i(TAG, "onTouchEvent: mLastX=" + mLastX);
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getX();
                mMoveDeltX = (int) (mCurrentX - mLastX);
                if (mMoveDeltX > 5 || mMoveDeltX < -5) {
                    mIsScrolled = true;
                }
//                Log.i(TAG, "onTouchEvent: mMoveDeltX=" + mMoveDeltX);
//                checkMarginLeft(marginLeft + mMoveDeltX);
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                toggle();
                if (listener != null)
                    listener.onCheckedChanged(this, isChecked());

                break;
        }
        return true;
    }

    public void setListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    OnCheckedChangeListener listener;

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        LogUtil.i("setchecked");
        super.setChecked(checked);
        checked = isChecked();
        if (checked) {
            checkMarginLeft(marginLeftMax);
        } else {
            checkMarginLeft(0);
        }
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return (super.verifyDrawable(who) || who == mBottomDrawable || who == mThumbDrawable);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        LogUtil.i("jumpDrawablesToCurrentState");
        if (Build.VERSION.SDK_INT >= 11) {
            super.jumpDrawablesToCurrentState();

            if (mThumbDrawable != null) {
                mThumbDrawable.jumpToCurrentState();
            }
            if (mBottomDrawable != null) {
                mBottomDrawable.jumpToCurrentState();
            }

        }
    }
}
