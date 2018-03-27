> 在工作中难免遇到自定义 `View` 的相关需求，本身这方面比较薄弱，因此做个记录，也是自己学习和成长的积累。[自定义View实战](https://link.jianshu.com/?t=http%3A%2F%2Fxiaweizi.cn%2Fcategories%2F%25E8%2587%25AA%25E5%25AE%259A%25E4%25B9%2589View%25E5%25AE%259E%25E6%2588%2598%2F)

 ## 前言

最近项目需要接入环信客服 `SDK` ，我配合这同事完成，其中我负责文件下载这部分。

因为时间比较紧张，8 天的时间完成 **环信客服模块** 的接入，就直接用了环信提供的 `UI` 控件，但是一些细节的部分， `UI` 还是会给出设计图，按照设计图完成最终效果。

`UI`那边直接让我参考 `IOS`的实现效果:

![UI效果](http://upload-images.jianshu.io/upload_images/4043475-ee6d3767cc22230c..png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 最终效果

![最终效果](http://upload-images.jianshu.io/upload_images/4043475-d1f9ca5534094c84..gif?imageMogr2/auto-orient/strip)

源码请看 [DownloadLoadingView](https://github.com/xiaweizi/DownloadLoadingView)

## 功能分析

面对这样的需要应该怎么实现呢？其实实现的方式可能不止我想的这种，我就讲述一下我是如何处理的。

![预览图](http://upload-images.jianshu.io/upload_images/4043475-52fe9c8f5f078c8a..png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

首先，可以分成三部分：

1. 半透明的背景
2. 全透明的环
3. 实心全透明的弧

那怎么实现背景半透明，而圆环和弧又是全透明的。顿时有个想法，要是两张图片重叠的部分能被抠出掉，也就是变成全透明，那岂不是非常容易就实现了。

圆环和弧既然是盖在了背景上，理当直接变成透明。那 `Android` 有对应处理的 `API`吗？答案是肯定的。 **setXfermode()** 用于设置图像的过度模式，其中 **PorterDuff.Mode.CLEAR** 为清除模式则可以实现上述的效果。

## 具体实现

### 一系列的初始化

```java
public DownloadLoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownloadLoadingView);
    mRadius = typedArray.getDimension(R.styleable.DownloadLoadingView_radius, RADIUS_DEFAULT);
    mStrokeWidth = typedArray.getDimension(R.styleable.DownloadLoadingView_strokeWidth, STROKE_WIDTH_DEFAULT);
    mMaxProgress = typedArray.getInteger(R.styleable.DownloadLoadingView_maxProgress, MAX_PROGRESS_DEFAULT);
    mRoundRadius = typedArray.getDimension(R.styleable.DownloadLoadingView_roundRadius, ROUND_RADIUS_DEFAULT);
    mBackgroundColor = typedArray.getColor(R.styleable.DownloadLoadingView_backgroundColor, getResources().getColor(R.color.bg_default));
    Log.i(TAG, "radius:" + mRadius);
    typedArray.recycle();
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
}
```

> 记得需要关闭硬件加速，不然会没有效果。

### 绘制背景

```java
paint.setColor(mBackgroundColor);
paint.setStyle(Paint.Style.FILL);
RectF round = new RectF(0, 0, getWidth(), getHeight());
canvas.drawRoundRect(round, mRoundRadius, mRoundRadius, paint);
```

> 设置背景颜色，样式为填充，绘制圆角矩形

### 绘制圆环

```java
paint.setColor(Color.RED);
paint.setStrokeWidth(mStrokeWidth);
// 采用 clear 的方式
paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
paint.setStyle(Paint.Style.STROKE);
canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, paint);
```

> 圆环的颜色可以随意设置，毕竟最后会被抠除掉，
>
> 设置 `PorterDuff.Mode.CLEAR` 类型模式绘制圆环

### 绘制圆

```java
paint.setStyle(Paint.Style.FILL);
float sweepAngle = 360 * mProgress / mMaxProgress;
RectF rectF = new RectF(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, getWidth() / 2 + mRadius, getHeight() / 2 + mRadius);
canvas.drawArc(rectF, -90, sweepAngle, true, paint);
// 记得设置为 null 不然会没有效果
paint.setXfermode(null);
```

> 根据当前的进度绘制相对应的弧，并且结束的时候将 `Xfermode` 模式置为 `null`。

这样效果就结束了，贼简单。完整的代码请看 [DownloadLoadingView](https://github.com/xiaweizi/DownloadLoadingView)

## 拓展

文中提到了 `PorterDuff.Mode`，里面存储了大量的枚举，当我们需要处理图像的时候就会用到，但是对每种类型并没有特别的了解。每次使用的时候都需要查资料，然后确定到底需要使用哪种模式。

```java
public Xfermode setXfermode(Xfermode xfermode) {
    long xfermodeNative = 0;
    if (xfermode != null)
        xfermodeNative = xfermode.native_instance;
    native_setXfermode(mNativePaint, xfermodeNative);
    mXfermode = xfermode;
    return xfermode;。
}
```

具体的模式：

```java
public enum Mode {
    /** [0, 0] */
    CLEAR       (0),
    /** [Sa, Sc] */
    SRC         (1),
    /** [Da, Dc] */
    DST         (2),
    /** [Sa + (1 - Sa)*Da, Rc = Sc + (1 - Sa)*Dc] */
    SRC_OVER    (3),
    /** [Sa + (1 - Sa)*Da, Rc = Dc + (1 - Da)*Sc] */
    DST_OVER    (4),
    /** [Sa * Da, Sc * Da] */
    SRC_IN      (5),
    /** [Sa * Da, Sa * Dc] */
    DST_IN      (6),
    /** [Sa * (1 - Da), Sc * (1 - Da)] */
    SRC_OUT     (7),
    /** [Da * (1 - Sa), Dc * (1 - Sa)] */
    DST_OUT     (8),
    /** [Da, Sc * Da + (1 - Sa) * Dc] */
    SRC_ATOP    (9),
    /** [Sa, Sa * Dc + Sc * (1 - Da)] */
    DST_ATOP    (10),
    /** [Sa + Da - 2 * Sa * Da, Sc * (1 - Da) + (1 - Sa) * Dc] */
    XOR         (11),
    /** [Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + min(Sc, Dc)] */
    DARKEN      (12),
    /** [Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + max(Sc, Dc)] */
    LIGHTEN     (13),
    /** [Sa * Da, Sc * Dc] */
    MULTIPLY    (14),
    /** [Sa + Da - Sa * Da, Sc + Dc - Sc * Dc] */
    SCREEN      (15),
    /** Saturate(S + D) */
    ADD         (16),
    OVERLAY     (17);
    Mode(int nativeInt) {
        this.nativeInt = nativeInt;
    }
    /**
     * @hide
     */
    public final int nativeInt;
}
```

注释中已经说明了该模式到的透明度计算和颜色的计算方式，首先我们要了解一下基本的概念：

```js
Sa：全称为Source alpha，表示源图的Alpha通道；
Sc：全称为Source color，表示源图的颜色；
Da：全称为Destination alpha，表示目标图的Alpha通道；
Dc：全称为Destination color，表示目标图的颜色.
```

来看一下权威的展示图：

![mode预览图](http://upload-images.jianshu.io/upload_images/4043475-8145b82ef1958374..png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我觉得 [各个击破搞明白 PorterDuff.Mode](https://www.jianshu.com/p/d11892bbe055) 这篇文章写的特别好，不是很懂的小伙伴可以看一下，在这里也表示一下感谢。

[原文地址](http://xiaweizi.cn/categories/%E8%87%AA%E5%AE%9A%E4%B9%89View%E5%AE%9E%E6%88%98/)
