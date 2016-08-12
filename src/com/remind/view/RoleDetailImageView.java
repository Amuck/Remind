package com.remind.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.help.remind.R;
import com.remind.util.AppUtil;

/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。 设置颜色在xml布局文件中由自定义属性配置参数指定
 */
public class RoleDetailImageView extends ImageView implements Runnable {
    private int mBorderThickness = 0;
    private Context mContext;
    private int defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    boolean isRunning = false;

    private final static double TWO_PI = Math.PI * 2;
    private int width;

    private int height;

    private int[] mBitmap2;

    private int[] mBitmap1;

    private float wavelength = 36;
    private float amplitude = 10;
    private float phase = 0;
    private int radius = 5;

    private int radius2 = 0;
    private int icentreX;
    private int icentreY;

    private int alpha = 255;
    private boolean flag = true;

    private int SCALE = 1;

    private Bitmap image;

    public RoleDetailImageView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RoleDetailImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoleDetailImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        // init();
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.roundedimageview_border_thickness, 0);
        mBorderOutsideColor = a.getColor(R.styleable.roundedimageview_border_outside_color, defaultColor);
        mBorderInsideColor = a.getColor(R.styleable.roundedimageview_border_inside_color, defaultColor);
    }

    public void init() {

        Drawable drawable = getDrawable();
        Bitmap b = null;
        if (null == drawable) {
            drawable = getResources().getDrawable(R.drawable.white);
            b = AppUtil.drawableToBitmap(drawable);
        } else {
            b = ((BitmapDrawable) drawable).getBitmap();
        }

        image = b.copy(Bitmap.Config.ARGB_8888, true);

        width = image.getWidth() / SCALE;
        height = image.getHeight() / SCALE;

        mBitmap2 = new int[width * height];
        mBitmap1 = new int[width * height];

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, width, height, false);

        scaledBitmap.getPixels(mBitmap1, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            mBitmap2[i] = mBitmap1[i];
        }
        setImageBitmap(Bitmap.createBitmap(mBitmap2, 0, width, width, height, Bitmap.Config.ARGB_8888));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }
        int radius = 0;
        if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
        } else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderOutsideColor);
        } else {// 没有边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
    }

    /**
     * 获取裁剪后的圆形图片
     * 
     * @param radius半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

    private boolean transformInverse(int x, int y, int[] out) {
        int dx = x - icentreX;
        int dy = y - icentreY;
        int distance2 = dx * dx + dy * dy;

        if (distance2 > radius2) {
            out[0] = x;
            out[1] = y;
            out[2] = 0;
            return false;
        } else {
            float distance = (float) Math.sqrt(distance2);
            float amount = amplitude * (float) Math.sin(distance / wavelength * TWO_PI - phase / TWO_PI);
            // float amount = amplitude * (float)Math.sin(distance / WT - PW);
            amount *= (radius - distance) / radius;
            // if (distance != 0)
            // amount *= wavelength / distance;
            out[0] = (int) (x + dx * amount);
            out[1] = (int) (y + dy * amount);
            out[2] = (int) distance;
            return true;
        }
    }

    private void createNextBitmap() {
        int[] temp = new int[3];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                if (transformInverse(i, j, temp)) {
                    if (temp[0] >= width || temp[1] >= height || temp[0] < 0 || temp[1] < 0) {
                        mBitmap2[j * width + i] = 0x00000000;
                    } else {
                        // mBitmap2[j * width + i] = (mBitmap1[temp[1] * width +
                        // temp[0]] & 0x00ffffff)
                        // + (int)((1 - temp[2] / (float)radius) * alpha) << 24;
                        mBitmap2[j * width + i] = (mBitmap1[temp[1] * width + temp[0]] & 0x00ffffff) + (alpha << 24);
                    }
                } else {
                    if (temp[0] >= width || temp[1] >= height || temp[0] < 0 || temp[1] < 0) {
                        mBitmap2[j * width + i] = 0x00000000;
                    } else {
                        mBitmap2[j * width + i] = mBitmap1[temp[1] * width + temp[0]];
                    }
                }

            }
    }

    @Override
    public void run() {
        isRunning = true;
        while (flag) {

            try {
                Thread.sleep(30);
            } catch (Exception e) {
            }
            // filter.radius2++;
            phase += 8;
            radius += 6;
            // amplitude = 0.5f;
            amplitude /= 1.12;
            if (amplitude < 0.01) {
                stop();
                return;
            }
            radius2 = radius * radius;

            createNextBitmap();
            post(new Runnable() {

                @Override
                public void run() {
                    setImageBitmap(Bitmap.createBitmap(mBitmap2, 0, width, width, height, Bitmap.Config.ARGB_8888));

                }
            });

            postInvalidate();

        }
    }

    private void stop() {
        phase = 0;
        radius = 5;
        amplitude = 10;
        flag = false;
        isRunning = false;
    }

    public void start() {
        flag = true;

        int x = image.getWidth() / 2;
        int y = image.getHeight() / 2;
        icentreX = x / SCALE;
        icentreY = y / SCALE;

        Thread t = new Thread(this);
        t.start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getmBorderOutsideColor() {
        return mBorderOutsideColor;
    }

    public void setmBorderOutsideColor(int mBorderOutsideColor) {
        this.mBorderOutsideColor = mBorderOutsideColor;
        invalidate();
    }

    public int getmBorderInsideColor() {
        return mBorderInsideColor;
    }

    public void setmBorderInsideColor(int mBorderInsideColor) {
        this.mBorderInsideColor = mBorderInsideColor;
        invalidate();
    }
}
