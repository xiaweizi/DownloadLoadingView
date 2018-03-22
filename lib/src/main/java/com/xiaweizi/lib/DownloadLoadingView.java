package com.xiaweizi.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
    private static final float STROKE_WIDTH_DEFAULT = 5;
    private static final float RADIUS_DEFAULT = 50;
    private static final int MAX_PROGRESS_DEFAULT = 100;

    private Paint paint ;
    private float mStrokeWidth;
    private float mRadius;
    private int mProgress = 50;
    private int mMaxProgress = MAX_PROGRESS_DEFAULT;


    public DownloadLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.CustomView);
        mRadius = typedArray.getDimension(R.styleable.CustomView_radius, RADIUS_DEFAULT);
        mStrokeWidth = typedArray.getDimension(R.styleable.CustomView_strokeWidth, STROKE_WIDTH_DEFAULT);
        mMaxProgress = typedArray.getInteger(R.styleable.CustomView_maxProgress, MAX_PROGRESS_DEFAULT);
        Log.i(TAG, "radius:" + mRadius);
        typedArray.recycle();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        paint.setColor(Color.argb(100, 0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 12, 12, paint);

        // 绘制进度
        paint.setColor(Color.RED);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth()/2,getHeight()/2, mRadius, paint);
        paint.setStyle(Paint.Style.FILL);
        float sweepAngle = 360 * mProgress / mMaxProgress;
        RectF rectF = new RectF(getWidth()/2-mRadius, getHeight()/2-mRadius, getWidth()/2+mRadius, getHeight()/2 + mRadius );
        canvas.drawArc(rectF , -90, sweepAngle, true, paint);
        paint.setXfermode(null);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }
}

