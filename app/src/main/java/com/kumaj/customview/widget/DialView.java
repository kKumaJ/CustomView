package com.kumaj.customview.widget;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kumaj.customview.R;

import java.util.Map;

public class DialView extends View {

    private static final String TAG = "DialView";

    private static final int sProgressArcPadding = 128; //px
    private static final int sProgressBgArcWidth = 60;
    private static final int sProgressFgArcWidth = 32;

    private static final int sProgressBgStartAngle = 30;
    private static final int sProgressBgSweepAngle = -240;

    private static final int sProgressFgStartAngle = 20;
    private static final int sProgressFgSweepAngle = -220;

    private static final int sDialPadding = 24;
    private static final int sDialLength = 32;
    private static final int sScale = 100;
    private static final int sMinDiameter = 600;

    private static final int MEASURE_WIDTH = 0;
    private static final int MEASURE_HEIGHT = 1;

    private Circle mOutCircle;
    private int mOutermostCircleRadius;

    private Arc mProgressBgArc;
    private int mProgressArcPadding;
    private int mProgressBgArcWidth;
    private int mProgressBgArcColor;
    private int mProgressFgArcWidth;
    private int mProgressFgStartAngle;
    private int mProgressFgSweepAngle;
    private int mProgressBgStartAngle;
    private int mProgressBgSweepAngle;

    private Arc mProgressFgArc;
    private Map<Object, Integer> mColorMap;
    private ArgbEvaluator mArgbEvaluator;
    private int[] color = new int[sScale];

    //paint
    private Paint mPgBgPaint;
    private Paint mPgFgPaint;
    private Paint mDialPaint;

    private int mDialNums;

    //arrow
    private Drawable mArrow;
    private float mArrowX;
    private float mArrowY;
    private float mArrowDegree;
    private float mArrowRadius;

    //dial
    private boolean isDrawDial;

    //
    private float mPercentage;

    private float mPosX;
    private float mPosY;

    private OnDialViewChangeListener mListener;

    public void setDialViewChangeListener(OnDialViewChangeListener l) {
        mListener = l;
    }


    public DialView(Context context) {
        this(context, null);

    }

    public DialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidthOrHeight(widthMeasureSpec, MEASURE_WIDTH)
                , measureWidthOrHeight(heightMeasureSpec, MEASURE_HEIGHT));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //outermost circle
        int diameter = width >= height ? height : width;
        diameter = Math.max(diameter, sMinDiameter);
        mOutCircle.x = mOutCircle.y = diameter / 2;
        mOutCircle.mRadius = diameter / 2;

        //arrow
        mArrowRadius = mOutCircle.mRadius - mProgressArcPadding / 2;
        mArrowX = mOutCircle.x;
        mArrowY = mOutCircle.y - mArrowRadius;
        mArrowDegree = 180;
    }

    private int measureWidthOrHeight(int measureSpec, int type) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int padding = type == MEASURE_WIDTH ? getPaddingLeft() + getPaddingRight() : getPaddingBottom() + getPaddingTop();

        if (specMode == MeasureSpec.EXACTLY) {
            result = Math.max(specSize, sMinDiameter);
        } else { //wrap_content
            result = mOutermostCircleRadius * 2 + mProgressArcPadding * 2 + padding;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw the outermost circle
        // TODO: 16/7/22  Circle's radius should be custom ?
        canvas.drawCircle(mOutCircle.x, mOutCircle.y, mOutCircle.mRadius, mOutCircle.mPaint);

        //draw the circle point for test
        canvas.drawPoint(mOutCircle.x, mOutCircle.y, mDialPaint);

        //draw the background arc
        mPgBgPaint.setColor(mProgressBgArcColor);
        mPgBgPaint.setStrokeWidth(mProgressBgArcWidth);
        mPgBgPaint.setAntiAlias(true);
        mPgBgPaint.setStyle(Paint.Style.STROKE);
        mPgBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressBgArc.mPaint = mPgBgPaint;
        mProgressBgArc.mStarAngle = mProgressBgStartAngle;
        mProgressBgArc.mSweepAngle = mProgressBgSweepAngle;
        canvas.drawArc(mOutCircle.getRectF(), mProgressBgArc.mStarAngle, mProgressBgArc.mSweepAngle, false, mProgressBgArc.mPaint);

        //draw the foreground arc
        mPgFgPaint.setStrokeWidth(mProgressFgArcWidth);
        mPgFgPaint.setAntiAlias(true);
        mPgFgPaint.setStyle(Paint.Style.STROKE);
        mPgFgPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressFgArc.mStarAngle = mProgressFgStartAngle;
        mProgressFgArc.mSweepAngle = mProgressFgSweepAngle;
        float curDegree;
        float fraction;
        float sweepAngle = (1.0f / sScale * 1.0f) * mProgressFgArc.mSweepAngle;
        for (int i = 0; i < sScale; i++) {
            fraction = ((i + 1) * 1.0f) / (sScale * 1.0f);
            if (i == 0) curDegree = mProgressFgArc.mStarAngle;
            else {
                curDegree = mProgressFgArc.mStarAngle + fraction * mProgressFgArc.mSweepAngle;
            }
            mPgFgPaint.setColor(color[i]);
            canvas.drawArc(mOutCircle.getRectF(), curDegree, sweepAngle, false, mPgFgPaint);
        }

        //draw the scale
        if (isDrawDial) {
            // TODO: 16/7/26 the dial num should be custom
            canvas.save();
            float startDegree = 90 + mProgressFgArc.mStarAngle;
            canvas.rotate(startDegree, mOutCircle.x, mOutCircle.y);
            float startY = mOutCircle.y - mOutCircle.mRadius +
                    mProgressArcPadding + mProgressBgArc.mPaint.getStrokeWidth() / 2 + sDialPadding;
            float stopY = startY + sDialLength;
            float rotateDegree = (1.0f * mProgressFgArc.mSweepAngle) / (1.0f * mDialNums);
            for (int i = 0; i < mDialNums + 1; i++) {
                canvas.drawLine(mOutCircle.x, startY, mOutCircle.x, stopY, mDialPaint);
                canvas.rotate(rotateDegree, mOutCircle.x, mOutCircle.y);
            }
            canvas.restore();
        }

        //draw arrow

        canvas.save();
        canvas.translate(mArrowX, mArrowY);
        canvas.rotate(mArrowDegree);
        mArrow.setBounds(-28, -28, 28, 28); //r t l b
        mArrow.draw(canvas);
        canvas.restore();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DialView, defStyle, 0);
            mArrow = a.getDrawable(R.styleable.DialView_arrowSrc);
            if (mArrow == null) mArrow = getResources().getDrawable(R.drawable.ic_arrow);

            mProgressArcPadding = a.getDimensionPixelSize
                    (R.styleable.DialView_progressArcPadding, sProgressArcPadding);

            mProgressBgArcWidth = a.getDimensionPixelSize
                    (R.styleable.DialView_progressBackgroundWidth, sProgressBgArcWidth);

            mProgressBgArcColor = a.getColor
                    (R.styleable.DialView_progressBackgroundColor, getResources().getColor(R.color.color_E7E7E7));

            mProgressFgArcWidth = a.getDimensionPixelOffset
                    (R.styleable.DialView_progressForegroundWidth, sProgressFgArcWidth);

            mProgressBgStartAngle = a.getInteger
                    (R.styleable.DialView_progressBackgroundStartAngle, sProgressBgStartAngle);

            mProgressBgSweepAngle = a.getInteger
                    (R.styleable.DialView_progressBackgroundSweepAngle, sProgressBgSweepAngle);

            mProgressFgStartAngle = a.getInteger
                    (R.styleable.DialView_progressForegroundStartAngle, sProgressFgStartAngle);

            mProgressFgSweepAngle = a.getInteger
                    (R.styleable.DialView_progressForegroundSweepAngle, sProgressFgSweepAngle);

            mOutermostCircleRadius = a.getInteger
                    (R.styleable.DialView_outermostCircleRadius, sMinDiameter / 2);

            isDrawDial = a.getBoolean(R.styleable.DialView_isDrawDial, true);
            a.recycle();
        }

        final Paint p1 = new Paint();
        p1.setColor(getResources().getColor(R.color.color_E7E7E7));
        p1.setStrokeWidth(4.0f);
        p1.setAntiAlias(true);
        p1.setStyle(Paint.Style.STROKE);
        p1.setStrokeCap(Paint.Cap.ROUND);

        mOutCircle = new Circle();
        mOutCircle.mPaint = p1;

        mPgBgPaint = new Paint();
        mProgressBgArc = new Arc();

        mPgFgPaint = new Paint();
        mProgressFgArc = new Arc();

        //initial colors
        mArgbEvaluator = new ArgbEvaluator();

        for (int i = 0; i < sScale; i++) {
            float fraction = (i * 1.0f / sScale * 1.0f);
            color[i] = (int) mArgbEvaluator.evaluate(fraction, Color.RED, Color.BLUE);
        }

        //
        mDialPaint = new Paint();
        mDialPaint.setColor(Color.GRAY);
        mDialPaint.setStrokeWidth(3);
        mPgFgPaint.setAntiAlias(true);
        mPgFgPaint.setDither(true);
        mDialPaint.setStyle(Paint.Style.FILL);
        //
        mDialNums = 30;


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mPosX = event.getX();
        mPosY = event.getY();

        if (!ignoreTouch(mPosX, mPosY)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    updateOnTouch(mPosX, mPosY);
                    if (mListener != null) mListener.onStartChange(this);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(mPosX, mPosY);
                    if (mListener != null) mListener.onProgressUpdate(this);
                    break;
                case MotionEvent.ACTION_UP:
                    updateOnTouch(mPosX, mPosY);
                    if (mListener != null) mListener.onStartChange(this);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }
        return false;
    }

    private boolean ignoreTouch(float posX, float posY) {
        //ignore the  touch event inside the progress background arc
        float dx = posX - mOutCircle.x;
        float dy = posY - mOutCircle.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < mOutCircle.mRadius - mProgressArcPadding;
    }

    private void updateOnTouch(float posX, float posY) {
        mArrowDegree = getTouchDegree(posX, posY);
        float arrowXDegree = -mArrowDegree;
        float radians = (float) Math.toRadians(arrowXDegree);

        Log.e(TAG, " degree = " + mArrowDegree);
        mArrowDegree += 270;
        mArrowX = (float) (mArrowRadius * Math.cos(radians)) + mOutCircle.mRadius;
        mArrowY = (float) (mArrowRadius * -Math.sin(radians)) + mOutCircle.mRadius;

        invalidate();
    }

    // 0->-180 180->0
    private float getTouchDegree(float posX, float posY) {
        float x = posX - mOutCircle.x;
        float y = posY - mOutCircle.y;
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    private float calculatePercentage(float posX, float posY) {
        float degree = getTouchDegree(posX, posY);
        float sweepDegree;
        if (degree <= 160f && degree >= 90f) {
            return 1f;
        } else if (degree < 90f && degree > 20f) {
            return 0f;
        } else if (degree <= 20f && degree >= -180f) {
            sweepDegree = 20f - degree;
        } else {
            sweepDegree = 200f + (180f - degree);
        }
        return sweepDegree / 220;
    }

    public float getPercentage() {
        mPercentage = calculatePercentage(mPosX, mPosY);
        return mPercentage;
    }

    public void setProgressArcPadding(int progressArcPadding) {
        this.mProgressArcPadding = progressArcPadding;
    }

    public void setProgressBgArcWidth(int progressBgArcWidth) {
        this.mProgressBgArcWidth = progressBgArcWidth;
    }

    public void setProgressFgArcWidth(int progressFgArcWidth) {
        this.mProgressFgArcWidth = progressFgArcWidth;
    }

    public void setProgressBgStartAngle(int progressBgStartAngle) {
        this.mProgressBgStartAngle = progressBgStartAngle;
    }

    public void setProgressBgSweepAngle(int progressBgSweepAngle) {
        this.mProgressBgSweepAngle = progressBgSweepAngle;
    }

    public void setProgressFgStartAngle(int progressFgStartAngle) {
        this.mProgressFgStartAngle = progressFgStartAngle;
    }

    public void setProgressFgSweepAngle(int progressFgSweepAngle) {
        this.mProgressFgSweepAngle = progressFgSweepAngle;
    }

    private class Arc {
        public int mStarAngle;
        public int mSweepAngle;
        public int mRadius;
        public Paint mPaint;
        public int x;
        public int y;

        public Arc() {
        }

        public Arc(int starAngle, int sweepAngle, Paint paint, int x, int y, int radius) {
            this.mStarAngle = starAngle;
            this.mPaint = paint;
            this.mRadius = radius;
        }

        public void setSweepAngle(int sweepAngle) {
            mSweepAngle = sweepAngle;
            invalidate();
        }

    }

    private class Circle extends Arc {

        public Circle() {
            super();
        }

        public Circle(Paint paint, int x, int y, int radius) {
            super(0, 360, paint, x, y, radius);
        }


        public RectF getRectF() {
            return new RectF(x - mRadius + mProgressArcPadding,
                    y - mRadius + mProgressArcPadding,
                    x + mRadius - mProgressArcPadding,
                    y + mRadius - mProgressArcPadding);
        }
    }

    public interface OnDialViewChangeListener {
        void onStartChange(DialView dialView);

        void onProgressUpdate(DialView dialView);

        void onStopChange(DialView dialView);
    }
}
