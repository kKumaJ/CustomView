package com.kumaj.loadingdrawable.octocat;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import com.kumaj.loadingdrawable.R;

public class OctoCatView extends AppCompatImageView {

    private OctoCatDrawable mOctoCatDrawable;


    public OctoCatView(Context context) {
        this(context, null);
    }


    public OctoCatView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.style);
    }


    public OctoCatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources res = getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OctoCatView,
            defStyleAttr, 0);

        final int color = a.getColor(R.styleable.OctoCatView_color,
            res.getColor(R.color.color_default));
        final int colorsId = a.getResourceId(R.styleable.OctoCatView_colos, 0);
        final float strokeWidth = a.getDimension(R.styleable.OctoCatView_strokeWidth,
            res.getDimension(R.dimen.octo_cat_default_stroke_width));
        final int section = a.getInteger(R.styleable.OctoCatView_section,
            res.getInteger(R.integer.octo_cat_default_section));
        final float intervalAngle = a.getFloat(R.styleable.OctoCatView_intervalAngle,
            res.getInteger(R.integer.octo_cat_default_interval_angle));
        a.recycle();

        int[] colors = null;
        if (colorsId != 0) {
            colors = res.getIntArray(colorsId);
        }

        OctoCatDrawable.Builder builder = new OctoCatDrawable.Builder(context)
            .setStrokeWidth(strokeWidth)
            .setSection(20)
            .setIntervalAngle(10f);

        if (colors != null && colors.length > 0) {
            builder.setColors(colors);
        } else {
            builder.setColor(color);
        }

        mOctoCatDrawable = builder.build();
        //
        setBackgroundDrawable(mOctoCatDrawable);
    }


    @Override protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }


    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }


    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }


    public void startAnimation() {
        if (mOctoCatDrawable != null) {
            mOctoCatDrawable.start();
        }
    }

    public void stopAnimation(){
        if (mOctoCatDrawable != null){
            mOctoCatDrawable.stop();
        }
    }
}
