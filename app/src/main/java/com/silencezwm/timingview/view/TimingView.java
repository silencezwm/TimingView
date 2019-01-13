package com.silencezwm.timingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.silencezwm.timingview.R;

import org.jetbrains.annotations.Nullable;

/**
 * @author silencezwm on 2019/1/12 10:41 AM
 * @email silencezwm@gmail.com
 * @description 自定义的计时View
 */
public class TimingView extends View {

    private static final String TAG = TimingView.class.getSimpleName();

    private Context mContext;

    //    private int mWidthMode;
//    private int mHeightMode;
    private int mWidthSize;
    private int mHeightSize;

    /**
     * circle radius
     */
    private int mCircleRadius;
    /**
     * 计时时间秒数
     */
    private long mTimingDuration;

    private float startAngle = 270;
    private float sweepAngle = 0;

    private Paint mStartPaint;
    private Paint mEndPaint;
    private BitmapShader mStartBitmapShader;
    private BitmapShader mEndBitmapShader;
    private Bitmap mStartBitmap;
    private Bitmap mEndBitmap;

    private ValueAnimator mValueAnimator;

    public TimingView(Context context) {
        this(context, null);
    }

    public TimingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttributeSet(attrs, defStyleAttr);
        initPaint();
        initAnimator();
    }

    private void initAttributeSet(@Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TimingView, defStyleAttr, 0);

        mTimingDuration = typedArray.getInt(R.styleable.TimingView_duration, 2);

        Drawable startDrawable = typedArray.getDrawable(R.styleable.TimingView_startDrawable);
        mStartBitmap = drawableToBitmap(startDrawable);
        Drawable endDrawable = typedArray.getDrawable(R.styleable.TimingView_endDrawable);
        mEndBitmap = drawableToBitmap(endDrawable);

        typedArray.recycle();


    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofInt(-360, 0);
        mValueAnimator.setDuration(mTimingDuration * 1000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sweepAngle = (int) valueAnimator.getAnimatedValue();
//                Log.d(TAG, String.format("currentValue is %f", sweepAngle));
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    private void initPaint() {
        mStartPaint = new Paint();
        mStartPaint.setAntiAlias(true);


        mEndPaint = new Paint();
        mEndPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = getMeasuredWidth();
        int measureHeight = getMeasuredHeight();

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                mWidthSize = widthSize;
                break;

            case MeasureSpec.AT_MOST:
                if (measureWidth <= 0){
                    mWidthSize = widthSize;
                }else {
                    mWidthSize = Math.min(widthSize, measureWidth);
                }
                break;

            case MeasureSpec.UNSPECIFIED:
                if (measureWidth <= 0){
                    mWidthSize = widthSize;
                }else {
                    mWidthSize = measureWidth;
                }
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                mHeightSize = heightSize;
                break;

            case MeasureSpec.AT_MOST:
                if (measureWidth <= 0){
                    mHeightSize = heightSize;
                }else {
                    mHeightSize = Math.min(heightSize, measureHeight);
                }
                break;

            case MeasureSpec.UNSPECIFIED:
                if (measureHeight <= 0){
                    mHeightSize = heightSize;
                }else {
                    mHeightSize = measureHeight;
                }
                break;
        }

        Log.d(TAG, "onMeasure"+"widthSize:"+mWidthSize+"-mHeightSize:"+mHeightSize);

        setMeasuredDimension(mWidthSize, mHeightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged:" + "w:" + w + " h:" + h + " oldw:" + oldw + " oldh:" + oldh);
        mStartBitmap = getResizedBitmap(mStartBitmap, w, h);
        mEndBitmap = getResizedBitmap(mEndBitmap, w, h);

        mStartBitmapShader = new BitmapShader(mStartBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mEndBitmapShader = new BitmapShader(mEndBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mStartPaint.setShader(mStartBitmapShader);
        mEndPaint.setShader(mEndBitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mEndBitmap, new Rect(0, 0, mWidthSize, mHeightSize),
                new RectF(0, 0, mWidthSize, mHeightSize), null);
        RectF startRectF = new RectF(0, 0, mWidthSize, mHeightSize);
        canvas.drawArc(startRectF, startAngle, sweepAngle, true, mStartPaint);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }

    }

    /**
     * convert a Drawable to a Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
