package net.suntrans.smarthome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import net.suntrans.smarthome.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Looney on 2017/7/6.
 */
public class GradeBar extends View {

    private int mBarPadding;
    private int mGradeNumber;
    private String[] mTextArray;

    private Paint mBarPaint;
    private Paint mTextPaint;
    private Paint mCirclePaint;

    private ViewConfiguration mScaledTouchSlop;
    private int space;
    private List<RectF> mIndexBounds;
    private int mCurrentIndex = 4;
    private int mThumbRadius;
    private boolean enable;

    public GradeBar(Context context) {
        this(context, null);
    }

    public int getmCurrentIndex() {
        return mCurrentIndex;
    }

    public void setmCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        invalidate();
    }

    public GradeBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradeBar, defStyleAttr, 0);
        mBarPadding = a.getDimensionPixelSize(R.styleable.GradeBar_barPadding, dip2px(16));
        mGradeNumber = a.getInteger(R.styleable.GradeBar_gradeNumber, 5);
        int id = a.getResourceId(R.styleable.GradeBar_textArray, R.array.textarray);
        mTextArray = context.getResources().getStringArray(id);
        mThumbRadius = dip2px(10);
        enable = a.getBoolean(R.styleable.GradeBar_enable, true);
        a.recycle();


        init(context);
        initObjects();

    }

    private void init(Context context) {
        mScaledTouchSlop = ViewConfiguration.get(context);
        mScaledTouchSlop.getScaledTouchSlop();
    }

    private void initObjects() {
        mBarPaint = new Paint();
        mBarPaint.setColor(Color.parseColor("#888888"));
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mBarPaint.setStrokeWidth(dip2px(5));


        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor("#ffffff"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.parseColor("#ffffff"));
        mCirclePaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = dip2px(100);
        int desiredHeight = dip2px(100);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {

            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {

            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            System.out.println("精确模式");
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            System.out.println("最大模式");

            height = Math.min(desiredHeight, heightSize);
        } else {
            System.out.println("未定义");

            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        space = (getMeasuredWidth() - 2 * mBarPadding) / (mGradeNumber-1);
        mIndexBounds = new ArrayList<>();
        for (int i = 0; i < mGradeNumber; i++) {
            RectF rectF = new RectF(mBarPadding + i * space - 3 * (mThumbRadius / 2), getMeasuredHeight() / 3 - (3 * mThumbRadius / 2), mBarPadding + i * space + 3 * mThumbRadius, getMeasuredHeight() / 3 + 3 * mThumbRadius / 2);
            mIndexBounds.add(rectF);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        drawText(canvas);
        drawCircle(canvas);
    }


    private void drawBg(Canvas canvas) {
        canvas.drawLine(0 + mBarPadding, getMeasuredHeight() / 3,
                getMeasuredWidth() - mBarPadding, getMeasuredHeight() / 3, mBarPaint);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < mGradeNumber; i++) {
            canvas.drawText(mTextArray[i], mBarPadding + i * space, getMeasuredHeight() * 2 / 3, mTextPaint);
        }
    }


    private void drawCircle(Canvas canvas) {
        mThumbRadius = dip2px(10);
        canvas.drawCircle(mBarPadding + mCurrentIndex * space, getMeasuredHeight() / 3, mThumbRadius, mCirclePaint);
    }


    public int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }


    public int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }


    private boolean isThumbFoucs = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (!enable) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isThumbFoucs = false;
                int lastX = (int) event.getX();
                int lastY = (int) event.getY();

                if (mIndexBounds.get(mCurrentIndex).contains(lastX, lastY)) {
                    isThumbFoucs = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                if (isThumbFoucs) {
//                    int x = (int) event.getX();
//                    int y = (int) event.getY();
//                    int tempIndex = -1;
//                    for (int i = 0; i < mIndexBounds.size(); i++) {
//                        if (mIndexBounds.get(i).contains(x, y)) {
//                            tempIndex = i;
//                        }
//                    }
//                    if (tempIndex != -1) {
//                        if (tempIndex != mCurrentIndex) {
//                            mCurrentIndex = tempIndex;
//                            invalidate();
//                            if (onGradeChangedListener != null) {
//                                onGradeChangedListener.onGradeChanged(mCurrentIndex);
//                            }
//                        }
//
//                    }
//                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isThumbFoucs) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int tempIndex = -1;
                    for (int i = 0; i < mIndexBounds.size(); i++) {
                        if (mIndexBounds.get(i).contains(x, y)) {
                            tempIndex = i;
//                            if (i > mCurrentIndex) {
//                                tempIndex = mCurrentIndex + 1;
//
//                            } else if (i < mCurrentIndex) {
//                                tempIndex = mCurrentIndex - 1;
//
//                            }
                        }
                    }
                    if (tempIndex != -1) {
                        if (tempIndex != mCurrentIndex) {
                            mCurrentIndex = tempIndex;
                            invalidate();
                            if (onGradeChangedListener != null) {
                                onGradeChangedListener.onGradeChanged(mCurrentIndex);
                            }
                        }

                    }
                }
                break;

        }
        return true;
    }


    public OnGradeChangedListener getOnGradeChangedListener() {
        return onGradeChangedListener;
    }

    public void setOnGradeChangedListener(OnGradeChangedListener onGradeChangedListener) {
        this.onGradeChangedListener = onGradeChangedListener;
    }

    private OnGradeChangedListener onGradeChangedListener;

    public interface OnGradeChangedListener {
        void onGradeChanged(int index);
    }
}
