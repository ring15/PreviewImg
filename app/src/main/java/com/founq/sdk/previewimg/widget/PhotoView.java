package com.founq.sdk.previewimg.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

import com.founq.sdk.previewimg.MatrixEvaluator;
import com.founq.sdk.previewimg.entity.MyMatrix;


/**
 * Created by ring on 2019/8/14.
 */
public class PhotoView extends AppCompatImageView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    //获取图片的路径
    private String mPath;

    //图片
    private Bitmap mBitmap;

    //缩放矩阵
    private Matrix mMatrix;

    //用来存放符合边界要求的矩阵，执行动画用
    private Matrix mTestMatrix;

    //控件的宽高
    private int mWidth, mHeight;

    //覆盖层圆半径
    private float radius;

    //手势检测
    private GestureDetector mDetector;

    //剪切图片的画笔
    private Paint clipPaint;

    //滑动事件，按下时的坐标
    private int lastX, lastY;

    //滑动事件模式（多点触摸，单点触摸）
    private int mode;

    //多点触摸时，初始两点之间距离
    private float startDistance;

    //判断是否为第一次双击
    private boolean isFirst = true;

    //判断是否为初始化
    private boolean isFirstInit = true;

    //动画
    private ValueAnimator animator;


    private changePage mChangePage;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mMatrix = new Matrix();
        mTestMatrix = new Matrix();
        mDetector = new GestureDetector(context, this);
        mDetector.setOnDoubleTapListener(this);
        clipPaint = new Paint();
    }

    public void setPath(String path) {
        mPath = path;
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        isFirstInit = true;
    }

    public void setChangePage(changePage changePage) {
        mChangePage = changePage;
    }

    public Bitmap clipBitmap() {
        Bitmap bitmap = Bitmap.createBitmap((int) (2 * radius), (int) (2 * radius), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(radius, radius, radius, clipPaint);
        clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float[] value = new float[9];
        mTestMatrix.getValues(value);
        float transX = value[Matrix.MTRANS_X];
        float transY = value[Matrix.MTRANS_Y];
        transX -= mWidth / 2 - radius;
        transY -= mHeight / 2 - radius;
        Matrix matrix = new Matrix();
        value[Matrix.MTRANS_X] = transX;
        value[Matrix.MTRANS_Y] = transY;
        matrix.setValues(value);
        canvas.drawBitmap(mBitmap, matrix, clipPaint);
        clipPaint.setXfermode(null);
        return bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        radius = (mWidth > mHeight) ? 2 * mHeight / 9 : 2 * mWidth / 9;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPath != null && mBitmap == null) {
            createBitmap();
        }
        if (isFirstInit) {
            matrix();
        }
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }

    private void matrix() {
        if (mBitmap != null) {
            mMatrix.setTranslate((mWidth - mBitmap.getWidth()) / 2, (mHeight - mBitmap.getHeight()) / 2);
            mTestMatrix.setTranslate((mWidth - mBitmap.getWidth()) / 2, (mHeight - mBitmap.getHeight()) / 2);
            isFirstInit = false;
        } else {
            invalidate();
        }
    }

    private void createBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//获取当前图片的边界大小，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(mPath, options);
        options.inJustDecodeBounds = false;
        //inSampleSize的作用就是可以把图片的长短缩小inSampleSize倍，所占内存缩小inSampleSize的平方
        float scale = calculateSampleSize(options, 720, 1280);
        options.inSampleSize = (int) scale;
        mBitmap = BitmapFactory.decodeFile(mPath, options);
        mMatrix.setScale(scale, scale);
        mMatrix.setTranslate((mWidth - mBitmap.getWidth()) / 2, (mHeight - mBitmap.getHeight()) / 2);
        mTestMatrix.setScale(scale, scale);
        mTestMatrix.setTranslate((mWidth - mBitmap.getWidth()) / 2, (mHeight - mBitmap.getHeight()) / 2);
    }

    private int startX;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (animator != null) {
            animator.end();
            animator = null;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                mode = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //计算多触点之间的间距
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                startDistance = (float) Math.sqrt(x * x + y * y);
                if (startDistance > 10f) {
                    mode = 2;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == 1) {
                    int endX = (int) event.getX();
                    if (startX - endX > getWidth() / 3) {
                        reset();
                        if (mChangePage != null) {
                            mChangePage.nextPage();
                        }
                        return true;
                    } else if (endX - startX > getWidth() / 3) {
                        reset();
                        if (mChangePage != null) {
                            mChangePage.prePage();
                        }
                        return true;
                    }
                }
                if (mode == 1 || mode == 2) {
                    float[] value = new float[9];
                    mMatrix.getValues(value);
                    final float[] testValue = new float[9];
                    mTestMatrix.getValues(testValue);
                    float scaleXvalue = value[Matrix.MSCALE_X];
                    float scaleYvalue = value[Matrix.MSCALE_Y];
                    float transXvalue = value[Matrix.MTRANS_X];
                    float transYvalue = value[Matrix.MTRANS_Y];
                    if (mPath != null) {
                        if (transXvalue < mWidth / 2 - radius && transYvalue < mHeight / 2 - radius &&
                                transXvalue + mBitmap.getWidth() * scaleXvalue > mWidth / 2 + radius &&
                                transYvalue + mBitmap.getHeight() * scaleYvalue > mHeight / 2 + radius &&
                                mBitmap.getWidth() * scaleXvalue > 2 * radius && mBitmap.getHeight() * scaleYvalue > 2 * radius &&
                                mBitmap.getWidth() * scaleXvalue < 10 * mBitmap.getWidth() && mBitmap.getHeight() * scaleYvalue < 10 * mBitmap.getHeight()
                        ) {
                            mTestMatrix.setValues(value);
                        } else {
                            reset();
                        }
                    } else {
                        if (transXvalue >= mWidth - mBitmap.getWidth() * scaleXvalue - (mWidth - mBitmap.getWidth()) / 2 &&
                                transYvalue >= mHeight - mBitmap.getHeight() * scaleYvalue - (mHeight - mBitmap.getHeight()) / 2 &&
                                transXvalue <= (mWidth - mBitmap.getWidth()) / 2 && transYvalue <= (mHeight - mBitmap.getHeight()) / 2 &&
                                mBitmap.getWidth() * scaleXvalue < 10 * mWidth && mBitmap.getWidth() * scaleXvalue > mWidth / 10 &&
                                mBitmap.getHeight() * scaleYvalue < 10 * mHeight && mBitmap.getHeight() * scaleYvalue > mHeight / 10
                        ) {
                            mTestMatrix.setValues(value);
                        } else if (mBitmap.getWidth() * scaleXvalue < 10 * mWidth && mBitmap.getWidth() * scaleXvalue > mWidth / 10 &&
                                mBitmap.getHeight() * scaleYvalue < 10 * mHeight && mBitmap.getHeight() * scaleYvalue > mHeight / 10) {
                            testValue[Matrix.MSCALE_X] = scaleXvalue;
                            testValue[Matrix.MSCALE_Y] = scaleYvalue;
                            testValue[Matrix.MTRANS_X] = (mWidth - mBitmap.getWidth() * scaleXvalue) / 2;
                            testValue[Matrix.MTRANS_Y] = (mHeight - mBitmap.getHeight() * scaleYvalue) / 2;
                            mTestMatrix.setValues(testValue);
                            animator = ValueAnimator.ofObject(new MatrixEvaluator(), new MyMatrix(value), new MyMatrix(testValue));
                            animator.setDuration(500);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    MyMatrix scale = (MyMatrix) valueAnimator.getAnimatedValue();
                                    mMatrix.setValues(scale.getValue());
                                    invalidate();
                                }
                            });
                            animator.start();
                        } else {
                            reset();
                        }
                    }
                }
                mode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == 1) {
                    int offsetX = (int) (event.getX() - lastX);
                    int offsetY = (int) (event.getY() - lastY);
                    lastX = (int) event.getX();
                    lastY = (int) event.getY();
                    mMatrix.postTranslate(offsetX, offsetY);
                    invalidate();
                } else if (mode == 2) {
                    float xEnd = event.getX(0) - event.getX(1);
                    float yEnd = event.getY(0) - event.getY(1);
                    float endDistance = (float) Math.sqrt(xEnd * xEnd + yEnd * yEnd);
                    if (endDistance > 10f) {
                        float scale = endDistance / startDistance;
                        startDistance = endDistance;
                        mMatrix.postScale(scale, scale,
                                (event.getX(0) + event.getX(1)) / 2,
                                (event.getY(0) + event.getY(1)) / 2);
                        invalidate();
                    }
                }
                break;
        }
        mDetector.onTouchEvent(event);
        return true;
    }

    private void reset() {

        float[] value = new float[9];
        mMatrix.getValues(value);
        final float[] testValue = new float[9];
        mTestMatrix.getValues(testValue);
        animator = ValueAnimator.ofObject(new MatrixEvaluator(), new MyMatrix(value), new MyMatrix(testValue));
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                MyMatrix scale = (MyMatrix) valueAnimator.getAnimatedValue();
                mMatrix.setValues(scale.getValue());
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * 计算出所需要压缩的大小
     *
     * @param options
     * @param reqWidth  我们期望的图片的宽，单位px
     * @param reqHeight 我们期望的图片的高，单位px
     * @return
     */
    private int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int sampleSize = 1;
        int picWidth = options.outWidth;
        int picHeight = options.outHeight;
        if (picWidth > reqWidth || picHeight > reqHeight) {
            int halfPicWidth = picWidth / 2;
            int halfPicHeight = picHeight / 2;
            while (halfPicWidth / sampleSize > reqWidth || halfPicHeight / sampleSize > reqHeight) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }

    /**
     * 当手指按下的时候触发下面的方法
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    /**
     * 当用户手指在屏幕上按下,而且还未移动和松开的时候触发这个方法
     *
     * @param motionEvent
     */
    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    /**
     * 当手指在屏幕上轻轻点击的时候触发下面的方法
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    /**
     * 当手指在屏幕上滚动的时候触发这个方法
     *
     * @param motionEvent
     * @param motionEvent1
     * @param v
     * @param v1
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /**
     * 当用户手指在屏幕上长按的时候触发下面的方法
     *
     * @param motionEvent
     */
    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    /**
     * 当用户的手指在触摸屏上拖过的时候触发下面的方法,velocityX代表横向上的速度,velocityY代表纵向上的速度
     *
     * @param motionEvent
     * @param motionEvent1
     * @param v
     * @param v1
     * @return
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /**
     * 当手机屏幕接收到单击事件,也就是当手指在点击了手机屏幕之后在一定时间内没有接受到第二次的单击事件时触发下面的方法
     * 当如果手机接收到的是双击事件,在每次双击的第一次的点击的时候会唯一的触发下面的方法
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    /**
     * 当系统接受到双击事件的时候会在两次点击之间触发一次下面的方法
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        float scale;
        mode = 3;
        if (animator != null) {
            animator.end();
            animator = null;
        }
        if (isFirst) {
            scale = 1.5f;
        } else {
            scale = 1 / 1.5f;
        }
        mMatrix.postScale(scale, scale,
                mWidth / 2,
                mHeight / 2);
        invalidate();
        isFirst = !isFirst;

        float[] value = new float[9];
        mMatrix.getValues(value);
        final float[] testValue = new float[9];
        mTestMatrix.getValues(testValue);
        float scaleXvalue = value[Matrix.MSCALE_X];
        float scaleYvalue = value[Matrix.MSCALE_Y];
        float transXvalue = value[Matrix.MTRANS_X];
        float transYvalue = value[Matrix.MTRANS_Y];

        if (transXvalue > mWidth - mBitmap.getWidth() * scaleXvalue - (mWidth - mBitmap.getWidth()) / 2 &&
                transYvalue > mHeight - mBitmap.getHeight() * scaleYvalue - (mHeight - mBitmap.getHeight()) / 2 &&
                transXvalue < (mWidth - mBitmap.getWidth()) / 2 && transYvalue < (mHeight - mBitmap.getHeight()) / 2 &&
                mBitmap.getWidth() * scaleXvalue < 10 * mWidth && mBitmap.getWidth() * scaleXvalue > mWidth / 10 &&
                mBitmap.getHeight() * scaleYvalue < 10 * mHeight && mBitmap.getHeight() * scaleYvalue > mHeight / 10
        ) {
            mTestMatrix.setValues(value);
        } else if (mBitmap.getWidth() * scaleXvalue < 10 * mWidth && mBitmap.getWidth() * scaleXvalue > mWidth / 10 &&
                mBitmap.getHeight() * scaleYvalue < 10 * mHeight && mBitmap.getHeight() * scaleYvalue > mHeight / 10) {
            testValue[Matrix.MSCALE_X] = scaleXvalue;
            testValue[Matrix.MSCALE_Y] = scaleYvalue;
            testValue[Matrix.MTRANS_X] = (mWidth - mBitmap.getWidth() * scaleXvalue) / 2;
            testValue[Matrix.MTRANS_Y] = (mHeight - mBitmap.getHeight() * scaleYvalue) / 2;
            mTestMatrix.setValues(testValue);
            animator = ValueAnimator.ofObject(new MatrixEvaluator(), new MyMatrix(value), new MyMatrix(testValue));
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    MyMatrix scale = (MyMatrix) valueAnimator.getAnimatedValue();
                    mMatrix.setValues(scale.getValue());
                    invalidate();
                }
            });
            animator.start();
        } else {
            reset();
        }

        return false;
    }

    /**
     * 当系统接收到双击事件的时候,在每一次的结束触发下面的方法一次
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public interface changePage {
        void nextPage();

        void prePage();
    }
}
