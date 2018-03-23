package com.xiaweizi.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * <pre>
 *     author : xiaweizi
 *     class  : com.xiaweizi.lib.DownloadLoadingView
 *     e-mail : 1012126908@qq.com
 *     time   : 2018/03/22
 *     desc   :
 * </pre>
 */

public class DownloadLoadingView extends View {

    private static final String TAG = "CustomView";
    /**
     * 默认的环的宽度
     */
    private static final float STROKE_WIDTH_DEFAULT = 5;
    /**
     * 默认的半径
     */
    private static final float RADIUS_DEFAULT = 50;
    /**
     * 默认最大的进度
     */
    private static final int MAX_PROGRESS_DEFAULT = 100;
    /**
     * 圆角的半径默认值
     */
    private static final int ROUND_RADIUS_DEFAULT = 12;

    private Paint paint;
    private float mStrokeWidth;
    private float mRadius;
    private float mRoundRadius;
    private int mProgress;
    private int mMaxProgress;
    private int mBackgroundColor;

    public DownloadLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomView);
        mRadius = typedArray.getDimension(R.styleable.CustomView_radius, RADIUS_DEFAULT);
        mStrokeWidth = typedArray.getDimension(R.styleable.CustomView_strokeWidth, STROKE_WIDTH_DEFAULT);
        mMaxProgress = typedArray.getInteger(R.styleable.CustomView_maxProgress, MAX_PROGRESS_DEFAULT);
        mRoundRadius = typedArray.getDimension(R.styleable.CustomView_roundRadius, ROUND_RADIUS_DEFAULT);
        mBackgroundColor = typedArray.getColor(R.styleable.CustomView_backgroundColor, getResources().getColor(R.color.bg_default));
        Log.i(TAG, "radius:" + mRadius);
        typedArray.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        paint.setColor(mBackgroundColor);
        paint.setStyle(Paint.Style.FILL);
        RectF round = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(round, mRoundRadius, mRoundRadius, paint);
        // 绘制圆环
        paint.setColor(Color.RED);
        paint.setStrokeWidth(mStrokeWidth);
        // 采用 clear 的方式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, paint);
        // 绘制内圆
        paint.setStyle(Paint.Style.FILL);
        float sweepAngle = 360 * mProgress / mMaxProgress;
        RectF rectF = new RectF(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, getWidth() / 2 + mRadius, getHeight() / 2 + mRadius);
        canvas.drawArc(rectF, -90, sweepAngle, true, paint);
        // 记得设置为 null 不然会没有效果
        paint.setXfermode(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        setMeasuredDimension(width > height ? height : width, width > height ? height : width);
    }

    /**
     * @param progress 设置进度
     */
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= mMaxProgress) {
            this.mProgress = progress;
            postInvalidate();
        }
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    /**
     * @param strokeWidth 设置圆环的宽度
     */
    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    /**
     * @param radius 设置圆的的半径
     */
    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    public float getRadius() {
        return this.mRadius;
    }

    /**
     * @param roundRadius 设置圆角的半径
     */
    public void setRoundRadius(float roundRadius) {
        this.mRoundRadius = roundRadius;
    }

    public float getRoundRadius() {
        return this.mRoundRadius;
    }

    /**
     * @param backgroundColor 设置背景颜色
     */
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }
}

